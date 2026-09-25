// Where Chrome puts the first baseline of the design's ui face at each size and line height, as
// px from the line's top — the ground truth `cssBaseline` (oldge-core type/OldgeText.kt) is held
// to. Renders through the reference wrapper, so fonts and CSS are the references' own (B-14).
//
//   node scripts/research/baseline-probe.mjs
import { readFileSync, writeFileSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { tmpdir } from 'node:os';
import { pathToFileURL } from 'node:url';
import { launch, wrap } from '../design-references.mjs';
import { compileTokensCss } from '../tokens-css.mjs';

const ROOT = resolve(import.meta.dirname, '../..');
const tokens = JSON.parse(readFileSync(join(ROOT, 'reference/design-system/tokens.json'), 'utf8'));
const cases = [];
for (const weight of [400, 700]) for (const size of [12, 13, 14, 15, 17]) for (const lh of [16, 20, 15.4, 22]) cases.push({ weight, size, lh });
const cells = cases.map((c, i) =>
  `<div id="c${i}" style="font: ${c.weight} ${c.size}px/${c.lh}px var(--font-ui); white-space: nowrap">Док<span style="display:inline-block;width:0;height:0"></span></div>`).join('');
const file = join(tmpdir(), 'oldge-baseline-probe.html');
writeFileSync(file, wrap(`<body><div style="padding:0">${cells}</div></body>`, 'toxic', { tokensCss: compileTokensCss(tokens), texture: 'off' }));
const { cdp, version, close } = await launch(process.env.CHROME);
try {
  const { targetId } = await cdp.send('Target.createTarget', { url: pathToFileURL(file).href });
  const { sessionId } = await cdp.send('Target.attachToTarget', { targetId, flatten: true });
  await new Promise((r) => setTimeout(r, 1500));
  const out = await cdp.send('Runtime.evaluate', {
    awaitPromise: true, returnByValue: true,
    expression: `document.fonts.ready.then(() => [...document.querySelectorAll('[id^=c]')].map((d) => {
      const box = d.getBoundingClientRect(), mark = d.querySelector('span').getBoundingClientRect();
      return [box.height, mark.bottom - box.top];
    }))`,
  }, sessionId);
  console.log(version);
  out.result.value.forEach(([h, b], i) => console.log(`${cases[i].weight} ${cases[i].size}px/${cases[i].lh}px  line ${h}  baseline ${b}`));
} finally {
  await close();
}
