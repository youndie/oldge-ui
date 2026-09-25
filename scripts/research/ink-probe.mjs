// Where Chrome draws the ink of the design's text, not only where its layout puts the baseline (B-46):
// for each size the tokens use, «НН» black on white in its own line box, photographed at 1×; the rows
// of the line box with any ink are the answer. Renders through the reference wrapper, so the faces,
// the smoothing and the CSS are the references' own. Pair with `InkMatchesChromeTest`, which asks Compose the
// same question.
//
//   node scripts/research/ink-probe.mjs
import { readFileSync, writeFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { tmpdir } from 'node:os';
import { pathToFileURL } from 'node:url';
import { launch, wrap } from '../design-references.mjs';
import { compileTokensCss } from '../tokens-css.mjs';

const ROOT = resolve(import.meta.dirname, '../..');
const tokens = JSON.parse(readFileSync(join(ROOT, 'reference/design-system/tokens.json'), 'utf8'));
// [family, weight, size, line]: every text style the tokens and the components use.
export const CASES = [
  ['ui', 400, 12, 16], ['ui', 700, 12, 16], ['ui', 700, 13, 16], ['ui', 400, 13, 18], ['ui', 700, 13, 20],
  ['ui', 400, 14, 18], ['ui', 400, 15, 20], ['ui', 700, 15, 20], ['ui', 400, 16, 20],
  ['title', 700, 17, 22], ['title', 700, 20, 24], ['title', 700, 28, 32],
  // The Avatar's initials: the title face at 36 % of the avatar, in a line of its own size.
  ['title', 700, 11.52, 11.52, 'АК'], ['title', 700, 15.84, 15.84, 'АК'], ['title', 700, 23.04, 23.04, 'АК'],
  // B-49: the lcd face (readouts, counts, the Stepper's digit, CodeInput) and the pixel face (tags,
  // the Switch lamp), each at the size and line its components set.
  ['lcd', 400, 12, 16], ['lcd', 400, 13, 16], ['lcd', 400, 14, 14], ['lcd', 400, 16, 20], ['lcd', 400, 20, 20], ['lcd', 400, 24, 24], ['lcd', 400, 28, 28], ['lcd', 400, 32, 32], ['lcd', 400, 34, 34],
  ['pixel', 400, 8, 10], ['pixel', 400, 10, 12],
  // With the companion face in the line: Cyrillic the design's lcd and pixel faces lack.
  ['lcd', 400, 14, 14, 'ГБ'], ['lcd', 400, 16, 20, 'ГБ'], ['pixel', 400, 10, 12, 'ИЛИ'],
];
const CELL = 48;
const cells = CASES.map(([family, weight, size, lh, text], i) =>
  `<div style="position:absolute; left:0; top:${i * CELL}px; width:120px; height:${lh}px; background:#fff; color:#000; font: ${weight} ${size}px/${lh}px var(--font-${family}); white-space: nowrap">${text ?? (family === 'lcd' || family === 'pixel' ? 'HH' : 'НН')}</div>`).join('');
const file = join(tmpdir(), 'oldge-ink-probe.html');
writeFileSync(file, wrap(`<body><div style="position:relative">${cells}</div></body>`, 'toxic', { tokensCss: compileTokensCss(tokens), texture: 'off' }));
const { cdp, version, close } = await launch(process.env.CHROME);
try {
  const { targetId } = await cdp.send('Target.createTarget', { url: pathToFileURL(file).href });
  const { sessionId } = await cdp.send('Target.attachToTarget', { targetId, flatten: true });
  await cdp.send('Emulation.setDeviceMetricsOverride', { width: 200, height: CASES.length * CELL, deviceScaleFactor: 1, mobile: false }, sessionId);
  await cdp.send('Runtime.evaluate', { awaitPromise: true, expression: 'document.fonts.ready' }, sessionId);
  await new Promise((r) => setTimeout(r, 1000));
  const shot = await cdp.send('Page.captureScreenshot', { format: 'png' }, sessionId);
  const png = join(tmpdir(), 'oldge-ink-probe.png');
  writeFileSync(png, Buffer.from(shot.data, 'base64'));
  // Read the ink rows in the page itself: draw the screenshot on a canvas and scan each cell.
  const out = await cdp.send('Runtime.evaluate', {
    awaitPromise: true, returnByValue: true,
    expression: `new Promise((ok) => { const img = new Image(); img.onload = () => {
      const c = document.createElement('canvas'); c.width = img.width; c.height = img.height; const g = c.getContext('2d'); g.drawImage(img, 0, 0);
      const cases = ${JSON.stringify(CASES)}; const res = cases.map(([f, w, s, lh], i) => { const top = i * ${CELL};
        const d = g.getImageData(0, top, 120, lh).data; const rows = [];
        for (let y = 0; y < lh; y++) { let ink = 0; for (let x = 0; x < 120; x++) { const p = (y * 120 + x) * 4; ink = Math.max(ink, 255 - d[p]); } rows.push(ink); }
        const inked = rows.map((v, y) => [v, y]).filter(([v]) => v > 96).map(([, y]) => y);
        const mass = rows.reduce((a, v, y) => a + v * y, 0) / rows.reduce((a, v) => a + v, 0);
        return [inked[0], inked[inked.length - 1], +mass.toFixed(2)]; }); ok(res); }; img.src = 'data:image/png;base64,${shot.data}'; })`,
  }, sessionId);
  console.log(version);
  out.result.value.forEach(([top, bottom, mass], i) => {
    const [f, w, s, lh, text] = CASES[i];
    console.log(`${f} ${w} ${s}px/${lh}px ${text ?? ''}  ink ${top}..${bottom}  centre ${mass}`);
  });
} finally {
  await close();
}
