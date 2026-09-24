// B-04: render the design system's two noise tiles alone in headless Chrome, at device scale 1, on
// a transparent background, so the PNG's alpha channel is exactly the mask the CSS applies.
//
//   node scripts/research/noise-tiles.mjs <out dir>
//
// The SVGs are read out of bundle.css (--og-grain-mask, --og-speckle-mask), not retyped.
import { readFileSync, writeFileSync, mkdirSync } from 'node:fs';
import { join, resolve } from 'node:path';
import { launch } from '../design-references.mjs';

const ROOT = resolve(import.meta.dirname, '../..');
const css = readFileSync(join(ROOT, 'reference/design-system/components/bundle.css'), 'utf8');
const svg = (name) => decodeURIComponent(new RegExp(`--og-${name}-mask: url\\("data:image/svg\\+xml,([^"]+)"\\)`).exec(css)[1]);
const out = process.argv[2] ?? join(ROOT, 'oldge-core/src/desktopTest/snapshots/design/texture');
mkdirSync(out, { recursive: true });

const { cdp, version, close } = await launch();
try {
  for (const [name, size] of [['grain', 160], ['speckle', 128]]) {
    const source = svg(name);
    writeFileSync(join(out, `${name}.svg`), source);
    const { targetId } = await cdp.send('Target.createTarget', { url: 'about:blank' });
    const { sessionId } = await cdp.send('Target.attachToTarget', { targetId, flatten: true });
    await cdp.send('Page.enable', {}, sessionId);
    await cdp.send('Emulation.setDeviceMetricsOverride', { width: size, height: size, deviceScaleFactor: 1, mobile: false }, sessionId);
    await cdp.send('Emulation.setDefaultBackgroundColorOverride', { color: { r: 0, g: 0, b: 0, a: 0 } }, sessionId);
    const loaded = cdp.once('Page.loadEventFired', sessionId);
    const html = `<!doctype html><html><body style="margin:0"><img style="display:block" src="data:image/svg+xml,${encodeURIComponent(source)}"></body></html>`;
    await cdp.send('Page.navigate', { url: `data:text/html,${encodeURIComponent(html)}` }, sessionId);
    await loaded;
    await cdp.send('Runtime.evaluate', { expression: 'document.images[0].decode()', awaitPromise: true }, sessionId);
    const shot = await cdp.send('Page.captureScreenshot', { format: 'png', clip: { x: 0, y: 0, width: size, height: size, scale: 1 } }, sessionId);
    writeFileSync(join(out, `${name}_chrome.png`), Buffer.from(shot.data, 'base64'));
    await cdp.send('Target.closeTarget', { targetId });
    console.log(name, size, 'rendered by', version);
  }
} finally {
  await close();
}
