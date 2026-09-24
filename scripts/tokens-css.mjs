// Compile the design system's tokens.json into the tokens.css its previews expect.
//
// The artifact page generates tokens.css itself and does not publish it (research §1.2), so the
// reference renderer compiles it here, in the shape the Design System type's format.md specifies:
//
//   :root, [data-theme="<first>"] { --<color>; --<shadow> }
//   [data-theme="<id>"] { ... }                  one block per further theme
//   :root { --<space>; --<radius>; --<other>; --font-<family key> }
//   .<style> { font: ...; letter-spacing: ... }   one class per type style
//
// An alias "{name}" compiles to var(--name). A token with no value for a theme inherits the first
// theme's, which in CSS is simply "not re-declared in that theme's block".

/** @param {object} tokens the parsed tokens.json */
export function compileTokensCss(tokens) {
  const themes = tokens.color.themes.map((t) => t.id);
  const [first] = themes;
  const blocks = Object.fromEntries(themes.map((t) => [t, []]));
  for (const family of ['color', 'shadow']) {
    for (const token of tokens[family]?.tokens ?? []) {
      const byTheme = typeof token.value === 'string' ? { [first]: token.value } : token.value;
      for (const theme of themes) {
        if (byTheme[theme] !== undefined) blocks[theme].push(`--${token.name}: ${value(byTheme[theme])};`);
      }
    }
  }
  const root = [];
  for (const [family, content] of Object.entries(tokens)) {
    if (family === 'color' || family === 'shadow' || family === 'type') continue;
    if (!content || !Array.isArray(content.tokens)) continue;
    for (const token of content.tokens) root.push(`--${token.name}: ${token.value};`);
  }
  for (const [key, stack] of Object.entries(tokens.type?.families ?? {})) root.push(`--font-${key}: ${stack};`);
  const styles = [];
  for (const group of tokens.type?.groups ?? []) {
    for (const style of group.styles) {
      const family = style.family ?? group.family;
      const spacing = style.letterSpacing ? ` letter-spacing: ${style.letterSpacing};` : '';
      styles.push(`.${style.name} { font: ${style.fontWeight} ${style.fontSize}/${style.lineHeight} var(--font-${family});${spacing} }`);
    }
  }
  return [
    `:root, [data-theme="${first}"] { ${blocks[first].join(' ')} }`,
    ...themes.slice(1).map((t) => `[data-theme="${t}"] { ${blocks[t].join(' ')} }`),
    `:root { ${root.join(' ')} }`,
    ...styles,
  ].join('\n');
}

function value(v) {
  const alias = /^\{(.+)\}$/.exec(v);
  return alias ? `var(--${alias[1]})` : v;
}
