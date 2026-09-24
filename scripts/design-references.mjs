#!/usr/bin/env node
// Render the parity references from the design system's own previews (research D3, B-03).
//
//   node scripts/design-references.mjs [--only <glob>] [--texture off] [--out <dir>] [--chrome <binary>]
//
// For each reference/design-system/components/<Name>/preview.html (not Cover) and each skin, this
// writes <out>/<Name>_<Skin>.png and a manifest.json beside them. The preview is never edited: it is
// wrapped in a page that loads what the artifact's frame would load —
//
//   tokens.css     compiled here from tokens.json (the artifact does not publish its own)
//   bundle.css     with its Google Fonts @import removed
//   @font-face     the repository's bundled faces (B-02), and --font-* pointed at them, so the
//                  reference is drawn in the fonts the Compose side draws with (research §1.2, D6);
//                  lcd and pixel as a two-face stack so Chrome's per-glyph fallback reproduces the
//                  Compose side's per-run companion
//   React 18.3.1   the UMD builds vendored in scripts/vendor/, so a render needs no network
//   bundle.js      after a Date stub that pins "now" to NOW below (DatePicker marks "today")
//
// — and rendered by headless Chrome over the DevTools protocol with reduced motion and device scale
// 1, in the frame the artifact's card gives it: 390 px wide for a component (the og-demo maximum) and
// as tall as the card's @dsCard height, growing to fit the content; a screen is clipped to its
// .phone element. Node 24 has WebSocket, so no dependencies.

import { spawn } from 'node:child_process';
import { createHash } from 'node:crypto';
import { existsSync, mkdirSync, mkdtempSync, readFileSync, readdirSync, rmSync, writeFileSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { join, resolve } from 'node:path';
import { setTimeout as sleep } from 'node:timers/promises';
import { pathToFileURL } from 'node:url';
import { compileTokensCss } from './tokens-css.mjs';

const ROOT = resolve(import.meta.dirname, '..');
const DS = join(ROOT, 'reference/design-system');
const FONTS = join(ROOT, 'oldge-core/src/commonMain/composeResources/font');
const VENDOR = join(ROOT, 'scripts/vendor');
const SKINS = ['toxic', 'media', 'crystal'];
const WIDTH = 390;
/**
 * A wait after two animation frames before a page is measured or photographed. Without it one
 * render in 177 caught DatePicker's header mid-way through a 1 ms transition (B-07).
 */
const SETTLE_MS = 100;
/** "Now" for every preview: the DatePicker demo's own date, at noon so no time zone moves the day. */
const NOW = '2026-09-24T12:00:00';

const FACES = [
  ['DejaVu Sans Condensed', 'dejavu_sans_condensed.ttf', 400],
  ['DejaVu Sans Condensed', 'dejavu_sans_condensed_bold.ttf', 700],
  ['Fira Sans', 'fira_sans_bold.ttf', 700],
  ['Share Tech Mono', 'share_tech_mono.ttf', 400],
  ['PT Mono', 'pt_mono.ttf', 400],
  ['Silkscreen', 'silkscreen.ttf', 400],
  ['Tiny5', 'tiny5.ttf', 400],
];
const FAMILIES = {
  ui: '"DejaVu Sans Condensed"',
  title: '"Fira Sans"',
  lcd: '"Share Tech Mono", "PT Mono"',
  pixel: 'Silkscreen, Tiny5',
};

function args() {
  const a = { only: '*', texture: 'on', out: join(ROOT, 'oldge-core/src/desktopTest/snapshots/design'), previews: join(DS, 'components'), chrome: process.env.CHROME };
  const argv = process.argv.slice(2);
  for (let i = 0; i < argv.length; i += 2) a[argv[i].replace(/^--/, '')] = argv[i + 1];
  a.chrome ??= '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome';
  return a;
}

const glob = (pattern) => new RegExp(`^${pattern.replace(/[.+^${}()|[\]\\]/g, '\\$&').replace(/\*/g, '.*').replace(/\?/g, '.')}$`);
const skinName = (id) => id[0].toUpperCase() + id.slice(1);

/** The wrapper page for one preview in one skin. */
export function wrap(preview, skin, { tokensCss, texture }) {
  const url = (p) => pathToFileURL(p).href;
  const head = preview.match(/<style>[\s\S]*?<\/style>/g)?.join('\n') ?? '';
  const body = /<body>([\s\S]*)<\/body>/.exec(preview)?.[1];
  if (body === undefined) throw new Error('preview has no <body>');
  const bundleCss = readFileSync(join(DS, 'components/bundle.css'), 'utf8').replace(/^@import url\([^)]*fonts\.googleapis[^)]*\);\s*/m, '');
  const faces = FACES.map(([family, file, weight]) => `@font-face { font-family: "${family}"; src: url("${url(join(FONTS, file))}"); font-weight: ${weight}; }`).join('\n');
  const families = Object.entries(FAMILIES).map(([key, stack]) => `--font-${key}: ${stack};`).join(' ');
  const textureAttr = texture === 'off' ? ' data-og-texture="off"' : '';
  return `<!doctype html>
<html lang="ru" data-theme="${skin}"${textureAttr}>
<head><meta charset="utf-8">
<style>${faces}</style>
<style>${tokensCss}</style>
<style>:root { ${families} }</style>
<style>/* The body's background is painted on the canvas but positioned by the root element's box, whose
  height is the content's: a preview shorter than its card, or one whose content is all positioned,
  drew the glow at the content's corner or not at all. The design puts it in the screen's bottom-
  right corner, which is where the Compose body draws it; the root is made to fill the frame (B-07). */
html { min-height: 100%; }</style>
<style>${bundleCss}</style>
<style>/* The design system's own reduced-motion rule, applied to every element: bundle.css matches only
  [class*="og-"], so the TypingIndicator's dots (bare <i>) and a page's own classes (.media__*) kept
  moving and a render photographed whichever frame it reached (B-03). */
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after { animation-duration: 1ms !important; animation-iteration-count: 1 !important; animation-delay: 0ms !important; transition-duration: 1ms !important; transition-delay: 0ms !important; }
}</style>
<script>(function () { var fixed = new Date('${NOW}').getTime(), Real = Date;
  function D(a, b, c, d, e, f, g) { return arguments.length ? new (Function.prototype.bind.apply(Real, [null].concat([].slice.call(arguments))))() : new Real(fixed); }
  D.prototype = Real.prototype; D.now = function () { return fixed; }; D.parse = Real.parse; D.UTC = Real.UTC; window.Date = D; })();</script>
<script src="${url(join(VENDOR, 'react.production.min.js'))}"></script>
<script src="${url(join(VENDOR, 'react-dom.production.min.js'))}"></script>
<script src="${url(join(DS, 'components/bundle.js'))}"></script>
${head}
</head>
<body>${body}</body>
</html>`;
}

export class Cdp {
  constructor(ws) {
    this.ws = ws;
    this.id = 0;
    this.pending = new Map();
    this.listeners = [];
    ws.addEventListener('message', (event) => {
      const msg = JSON.parse(event.data);
      if (msg.id && this.pending.has(msg.id)) {
        const { resolve: ok, reject } = this.pending.get(msg.id);
        this.pending.delete(msg.id);
        msg.error ? reject(new Error(`${msg.error.message} ${msg.error.data ?? ''}`)) : ok(msg.result);
      } else {
        for (const l of this.listeners) l(msg);
      }
    });
  }

  send(method, params = {}, sessionId) {
    const id = ++this.id;
    this.ws.send(JSON.stringify({ id, method, params, sessionId }));
    return new Promise((ok, reject) => this.pending.set(id, { resolve: ok, reject }));
  }

  once(method, sessionId) {
    return new Promise((ok) => {
      const l = (msg) => {
        if (msg.method === method && msg.sessionId === sessionId) {
          this.listeners.splice(this.listeners.indexOf(l), 1);
          ok(msg.params);
        }
      };
      this.listeners.push(l);
    });
  }
}

export async function launch(chrome = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome") {
  const profile = mkdtempSync(join(tmpdir(), 'oldge-refs-'));
  const proc = spawn(chrome, [
    '--headless=new', '--remote-debugging-port=0', `--user-data-dir=${profile}`, '--allow-file-access-from-files',
    '--force-prefers-reduced-motion', '--force-device-scale-factor=1', '--hide-scrollbars', '--disable-gpu',
    '--no-first-run', '--no-default-browser-check', 'about:blank',
  ], { stdio: 'ignore' });
  const portFile = join(profile, 'DevToolsActivePort');
  for (let i = 0; i < 100 && !existsSync(portFile); i++) await sleep(100);
  const [port, path] = readFileSync(portFile, 'utf8').trim().split('\n');
  const ws = new WebSocket(`ws://127.0.0.1:${port}${path}`);
  await new Promise((ok, fail) => { ws.onopen = ok; ws.onerror = fail; });
  const version = await fetch(`http://127.0.0.1:${port}/json/version`).then((r) => r.json());
  const exited = new Promise((ok) => proc.once('exit', ok));
  const close = async () => {
    ws.close();
    proc.kill();
    // Chrome keeps writing its profile until it has exited; removing it earlier races that.
    await exited;
    rmSync(profile, { recursive: true, force: true, maxRetries: 5 });
  };
  return { cdp: new Cdp(ws), version: version.Browser, close };
}

async function render(cdp, file, width, page, frameHeight) {
  const { targetId } = await cdp.send('Target.createTarget', { url: 'about:blank' });
  const { sessionId } = await cdp.send('Target.attachToTarget', { targetId, flatten: true });
  const errors = [];
  const onError = (msg) => {
    if (msg.sessionId !== sessionId) return;
    if (msg.method === 'Runtime.exceptionThrown') errors.push(msg.params.exceptionDetails.exception?.description ?? msg.params.exceptionDetails.text);
    if (msg.method === 'Log.entryAdded' && msg.params.entry.level === 'error') errors.push(msg.params.entry.text);
  };
  cdp.listeners.push(onError);
  try {
    await cdp.send('Page.enable', {}, sessionId);
    await cdp.send('Runtime.enable', {}, sessionId);
    await cdp.send('Log.enable', {}, sessionId);
    await cdp.send('Emulation.setEmulatedMedia', { features: [{ name: 'prefers-reduced-motion', value: 'reduce' }] }, sessionId);
    // The frame is the card's: as tall as the preview's @dsCard height, growing to fit its content,
    // which is how the artifact shows it. That height is not cosmetic — BottomSheet's body is
    // `max-height: 70vh`, so a preview's layout can depend on the frame it is shown in (B-03).
    await cdp.send('Emulation.setDeviceMetricsOverride', { width, height: frameHeight, deviceScaleFactor: 1, mobile: false }, sessionId);
    const loaded = cdp.once('Page.loadEventFired', sessionId);
    await cdp.send('Page.navigate', { url: pathToFileURL(file).href }, sessionId);
    await loaded;
    const measure = async () => JSON.parse((await cdp.send('Runtime.evaluate', {
      awaitPromise: true,
      returnByValue: true,
      expression: `document.fonts.ready.then(() => new Promise((r) => requestAnimationFrame(() => requestAnimationFrame(() => setTimeout(() => r(JSON.stringify({
        w: document.documentElement.scrollWidth, h: document.documentElement.scrollHeight,
        fonts: [...document.fonts].map((f) => f.family + ' ' + f.weight + ' ' + f.status),
        text: document.body.innerText.trim().length,
        phone: (() => { const e = document.querySelector('.phone'); if (!e) return null; const r = e.getBoundingClientRect();
          return { x: Math.round(r.x), y: Math.round(r.y), w: Math.round(r.width), h: Math.round(r.height) }; })() })), ${SETTLE_MS})))))`,
    }, sessionId)).result.value);
    let m = await measure();
    const w = Math.max(width, m.w);
    await cdp.send('Emulation.setDeviceMetricsOverride', { width: w, height: frameHeight, deviceScaleFactor: 1, mobile: false }, sessionId);
    m = await measure();
    await cdp.send('Emulation.setDeviceMetricsOverride', { width: w, height: m.h, deviceScaleFactor: 1, mobile: false }, sessionId);
    m = { ...(await measure()), w };
    // A page is the phone and nothing around it: the card's margin and the window shadow are the
    // artifact's presentation, and the Compose fixture is the screen alone.
    if (page && !m.phone) throw new Error(`${file}: a page preview with no .phone element`);
    const clip = page ? { x: m.phone.x, y: m.phone.y, width: m.phone.w, height: m.phone.h } : { x: 0, y: 0, width: w, height: m.h };
    const shot = await cdp.send('Page.captureScreenshot', { format: 'png', clip: { ...clip, scale: 1 } }, sessionId);
    return { png: Buffer.from(shot.data, 'base64'), width: clip.width, height: clip.height, fonts: m.fonts, text: m.text, errors };
  } finally {
    cdp.listeners.splice(cdp.listeners.indexOf(onError), 1);
    await cdp.send('Target.closeTarget', { targetId });
  }
}

async function main() {
  const a = args();
  const tokens = JSON.parse(readFileSync(join(DS, 'tokens.json'), 'utf8'));
  const tokensCss = compileTokensCss(tokens);
  const index = JSON.parse(readFileSync(join(DS, 'design-system.json'), 'utf8'));
  const only = glob(a.only);
  // --previews scripts/probes renders this repository's material probes (B-07) the same way: small
  // pages built from the design system's own classes, so a material has a reference of its own.
  const previews = resolve(a.previews);
  const names = readdirSync(previews, { withFileTypes: true })
    .filter((d) => d.isDirectory() && d.name !== 'Cover' && existsSync(join(previews, d.name, 'preview.html')))
    .map((d) => d.name)
    .sort();
  const work = join(ROOT, 'build/design-references');
  mkdirSync(work, { recursive: true });
  mkdirSync(a.out, { recursive: true });
  const manifestFile = join(a.out, 'manifest.json');
  const manifest = existsSync(manifestFile) ? JSON.parse(readFileSync(manifestFile, 'utf8')) : { references: {} };
  const { cdp, version, close } = await launch(a.chrome);
  const warnings = [];
  try {
    for (const name of names) {
      const preview = readFileSync(join(previews, name, 'preview.html'), 'utf8');
      const page = /@dsCard[^>]*\bpage\b/.test(preview.split('\n')[0]);
      for (const skin of SKINS) {
        const stem = `${name}_${skinName(skin)}`;
        if (!only.test(stem)) continue;
        const file = join(work, `${stem}.html`);
        writeFileSync(file, wrap(preview, skin, { tokensCss, texture: a.texture }));
        const frameHeight = Number(/@dsCard[^>]*\bheight=(\d+)/.exec(preview.split('\n')[0])?.[1] ?? 120);
        const r = await render(cdp, file, WIDTH, page, frameHeight);
        writeFileSync(join(a.out, `${stem}.png`), r.png);
        const failedFonts = r.fonts.filter((f) => f.endsWith(' error'));
        const notes = [...r.errors.map((e) => `error: ${e}`), ...failedFonts.map((f) => `font failed to load: ${f}`)];
        if (r.text === 0) notes.push('no text rendered');
        if (/\bnew Date\(\)|DatePicker/.test(preview)) notes.push(`Date pinned to ${NOW}`);
        for (const n of notes) warnings.push(`${stem}: ${n}`);
        manifest.references[stem] = {
          ...(previews === join(DS, 'components') ? {} : { source: `${previews.slice(ROOT.length + 1)}/${name}/preview.html` }),
          width: r.width,
          height: r.height,
          page,
          sha256: createHash('sha256').update(r.png).digest('hex'),
          ...(notes.length ? { notes } : {}),
        };
        console.log(`${stem.padEnd(28)} ${String(r.width).padStart(4)}x${String(r.height).padEnd(5)} ${notes.join('; ')}`);
      }
    }
  } finally {
    await close();
  }
  manifest.designSystem = { artifact: 'CL8BafGgX4GgYdJXNEZttC', lastChange: index.lastChange?.at };
  manifest.chrome = version;
  manifest.texture = a.texture;
  manifest.wrapper = [
    'tokens.css compiled from tokens.json by scripts/tokens-css.mjs (the artifact publishes none)',
    'fonts: the bundled faces of B-02 in place of Tahoma / Trebuchet MS and the Google Fonts @import (research D6)',
    'prefers-reduced-motion, with the design system\'s own reduced-motion rule applied to every element, not only [class*="og-"]',
    'frame: 390 px wide, the @dsCard height growing to fit; a page is clipped to its .phone element',
    'html { min-height: 100% }: the body background (and its glow) spans the frame, not the content',
    `Date pinned to ${NOW}`,
  ];
  manifest.now = NOW;
  manifest.references = Object.fromEntries(Object.entries(manifest.references).sort(([x], [y]) => x.localeCompare(y)));
  writeFileSync(manifestFile, `${JSON.stringify(manifest, null, 2)}\n`);
  if (warnings.length) console.log(`\n${warnings.length} warning(s):\n  ${warnings.join('\n  ')}`);
}

if (import.meta.url === pathToFileURL(process.argv[1]).href) await main();
