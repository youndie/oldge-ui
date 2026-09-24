// node --test scripts/
import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import { test } from 'node:test';
import { compileTokensCss } from './tokens-css.mjs';

const vendored = JSON.parse(readFileSync(new URL('../reference/design-system/tokens.json', import.meta.url)));
const css = compileTokensCss(vendored);
const block = (selector) => css.split('\n').find((line) => line.startsWith(selector)) ?? '';

test('a per-skin colour lands in each skin block, the first skin on :root too', () => {
  assert.match(block(':root, [data-theme="toxic"]'), /--accent: #a4ff1f;/);
  assert.match(block('[data-theme="crystal"]'), /--accent: #8fe010;/);
});

test('a spacing token lands on :root with its unit', () => {
  assert.match(block(':root {'), /--space-4: 16px;/);
});

test('a font family becomes --font-<key>', () => {
  assert.match(block(':root {'), /--font-lcd: "Share Tech Mono"/);
});

test('a type style becomes a class with its family, and letter spacing when it has one', () => {
  assert.match(css, /^\.display \{ font: 700 1\.75rem\/2rem var\(--font-title\); \}$/m);
  assert.match(css, /^\.pixel-tag \{ font: 400 0\.625rem\/0\.75rem var\(--font-pixel\); letter-spacing: 0\.06em; \}$/m);
});

test('an alias compiles to var() and a missing theme value is not re-declared', () => {
  // The vendored file has no alias, so the grammar is held on a two-token system of its own.
  const out = compileTokensCss({
    color: {
      themes: [{ id: 'a' }, { id: 'b' }],
      tokens: [
        { name: 'ink', value: { a: '#000', b: '#fff' } },
        { name: 'text', value: '{ink}' },
      ],
    },
  });
  assert.match(out, /:root, \[data-theme="a"\] \{ --ink: #000; --text: var\(--ink\); \}/);
  assert.match(out, /\[data-theme="b"\] \{ --ink: #fff; \}/);
});
