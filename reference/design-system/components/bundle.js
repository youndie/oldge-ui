/* @ds-bundle: {"format":4,"namespace":"OldgeUI","components":[{"name":"Button"},{"name":"OrbButton"},{"name":"Segmented"},{"name":"WindowBar"},{"name":"CategoryTabs"},{"name":"BottomNav"},{"name":"Panel"},{"name":"Accordion"},{"name":"List"},{"name":"ListItem"},{"name":"ActionTile"},{"name":"Dialog"},{"name":"TextField"},{"name":"Select"},{"name":"Checkbox"},{"name":"RadioGroup"},{"name":"Switch"},{"name":"Slider"},{"name":"ProgressBar"},{"name":"Meter"},{"name":"Readout"},{"name":"Badge"},{"name":"Balloon"},{"name":"Card"},{"name":"Chip"},{"name":"Fab"},{"name":"Menu"},{"name":"BottomSheet"},{"name":"NavDrawer"},{"name":"Tabs"},{"name":"Snackbar"},{"name":"Tooltip"},{"name":"SearchBar"},{"name":"Spinner"},{"name":"Divider"},{"name":"Avatar"},{"name":"Stepper"},{"name":"DatePicker"},{"name":"Banner"},{"name":"Skeleton"},{"name":"CodeInput"},{"name":"EmptyState"},{"name":"ListSection"},{"name":"SwipeRow"},{"name":"PullRefresh"},{"name":"ChatBubble"},{"name":"TypingIndicator"},{"name":"Composer"},{"name":"PageDots"},{"name":"Icon"}]} */
(function () {
  var R = window.React, h = R.createElement;
  var uid = 0;
  function useId(prefix) { var r = R.useRef(null); if (r.current === null) r.current = (prefix || 'og') + '-' + (++uid); return r.current; }
  function cx() { return Array.prototype.filter.call(arguments, Boolean).join(' '); }
  function omit(o, keys) { var r = {}; for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k) && keys.indexOf(k) < 0) r[k] = o[k]; return r; }
  function clamp01(v) { v = Number(v) || 0; return v < 0 ? 0 : v > 1 ? 1 : v; }
  function reduced() { try { return window.matchMedia('(prefers-reduced-motion: reduce)').matches; } catch (e) { return false; } }
  /* Press effect: a gloss flash swelling from the touch point, clipped to the host.
     data-og-press="none" on any ancestor (or <html>) turns it off. */
  function pressFx(e, sel) {
    if (reduced() || e.button > 0) return;
    var host = sel ? e.currentTarget.querySelector(sel) : e.currentTarget;
    if (!host || e.currentTarget.disabled) return;
    var off = host.closest('[data-og-press]');
    if (off && off.getAttribute('data-og-press') === 'none') return;
    var r = host.getBoundingClientRect(), size = Math.min(56, Math.max(28, Math.min(r.width, r.height)));  /* a small spot, not a flood */
    var d = document.createElement('span');
    d.className = 'og-glint';
    d.style.width = d.style.height = size + 'px';
    d.style.left = (e.clientX - r.left - size / 2) + 'px';
    d.style.top = (e.clientY - r.top - size / 2) + 'px';
    host.appendChild(d);
    d.addEventListener('animationend', function () { if (d.parentNode) d.parentNode.removeChild(d); });
  }
  function pressOn(user, sel) { return function (e) { pressFx(e, sel); if (user) user(e); }; }

  /* 24px grid, single ink, evenodd for holes */
  var PATHS = {
    home: 'M12 3l9.5 8.5h-3V21h-5v-6h-3v6h-5v-9.5h-3z',
    search: 'M10 3a7 7 0 1 0 4.2 12.6l5.1 5.1 1.7-1.7-5.1-5.1A7 7 0 0 0 10 3zm0 2.4a4.6 4.6 0 1 1 0 9.2 4.6 4.6 0 0 1 0-9.2z',
    gear: 'M19.6 10.3 22.1 10.7 22.1 13.3 19.6 13.7 18.6 16.2 20.1 18.2 18.2 20.1 16.2 18.6 13.7 19.6 13.3 22.1 10.7 22.1 10.3 19.6 7.8 18.6 5.8 20.1 3.9 18.2 5.4 16.2 4.4 13.7 1.9 13.3 1.9 10.7 4.4 10.3 5.4 7.8 3.9 5.8 5.8 3.9 7.8 5.4 10.3 4.4 10.7 1.9 13.3 1.9 13.7 4.4 16.2 5.4 18.2 3.9 20.1 5.8 18.6 7.8zM12 8.6a3.4 3.4 0 1 0 0 6.8 3.4 3.4 0 0 0 0-6.8z',
    user: 'M12 3a4.5 4.5 0 1 1 0 9 4.5 4.5 0 0 1 0-9zM3.5 21c0-4.2 3.8-7 8.5-7s8.5 2.8 8.5 7z',
    bell: 'M12 2.5c.8 0 1.4.6 1.4 1.4v.7c2.9.7 4.6 3.2 4.6 6.2v4.2l2 2.5v1H4v-1l2-2.5v-4.2c0-3 1.7-5.5 4.6-6.2v-.7c0-.8.6-1.4 1.4-1.4zM9.5 19.5h5a2.5 2.5 0 0 1-5 0z',
    star: 'M12 2.5l2.9 6 6.6.8-4.9 4.6 1.3 6.6L12 17.2l-5.9 3.3 1.3-6.6-4.9-4.6 6.6-.8z',
    doc: 'M5 2.5h9l5 5v14H5zM13.2 4v4.3h4.3zM7.5 11h9v1.6h-9zm0 3.2h9v1.6h-9zm0 3.2h6v1.6h-6z',
    image: 'M3 4.5h18v15H3zM5 6.5v9.3l4-4.3 3 3.2 2.2-2.2 4.8 4.8V6.5zM15.5 8a1.8 1.8 0 1 1 0 3.6 1.8 1.8 0 0 1 0-3.6z',
    note: 'M9 3.5v10.1A3.6 3.6 0 1 0 11 17V8h7V3.5z',
    film: 'M3 4h18v16H3zM5 6v2h2V6zm0 5v2h2v-2zm0 5v2h2v-2zM17 6v2h2V6zm0 5v2h2v-2zm0 5v2h2v-2zM9 6v12h6V6z',
    folder: 'M2.5 5.5A1.5 1.5 0 0 1 4 4h5.2l2 2.2H20a1.5 1.5 0 0 1 1.5 1.5v10.8A1.5 1.5 0 0 1 20 20H4a1.5 1.5 0 0 1-1.5-1.5z',
    disc: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm0 7.5a2.5 2.5 0 1 1 0 5 2.5 2.5 0 0 1 0-5zM12 4.5A7.5 7.5 0 0 0 4.5 12H6a6 6 0 0 1 6-6z',
    flame: 'M13.6 1.5c.4 3.6 4.9 5.8 4.9 11.4a6.5 6.5 0 0 1-13 0c0-3.1 1.6-5.3 3.3-6.8-.1 2.1.9 3.7 2.4 3.7-.1-3.2.7-5.9 2.4-8.3zM12 21.2c-1.9 0-3.3-1.4-3.3-3.3 0-1.9 1.3-3 3.3-5.1 2 2.1 3.3 3.2 3.3 5.1 0 1.9-1.4 3.3-3.3 3.3z',
    cloud: 'M7 19a5 5 0 0 1-.6-10A6.5 6.5 0 0 1 18.8 10.5 4.3 4.3 0 0 1 18 19z',
    lock: 'M7 10V7.5a5 5 0 0 1 10 0V10h1.5v11h-13V10zm2.4 0h5.2V7.5a2.6 2.6 0 0 0-5.2 0zM11 14h2v4h-2z',
    grid: 'M4 4h7v7H4zm9 0h7v7h-7zM4 13h7v7H4zm9 0h7v7h-7z',
    download: 'M10.8 3h2.4v9.2l3.3-3.3 1.7 1.7L12 16.8l-6.2-6.2 1.7-1.7 3.3 3.3zM4 18.5h16V21H4z',
    upload: 'M12 3l6.2 6.2-1.7 1.7-3.3-3.3V17h-2.4V7.6L7.5 10.9 5.8 9.2zM4 18.5h16V21H4z',
    refresh: 'M12 4a8 8 0 0 1 5.7 2.3L20 4v6.5h-6.5l2.5-2.5A5.6 5.6 0 1 0 17.4 13.5h2.4A8 8 0 1 1 12 4z',
    plus: 'M10.8 4h2.4v6.8H20v2.4h-6.8V20h-2.4v-6.8H4v-2.4h6.8z',
    minus: 'M4 10.8h16v2.4H4z',
    edit: 'M16.2 3.3l4.5 4.5L9 19.5l-5.5 1 1-5.5zM14.6 6.1l-1 1 3.3 3.3 1-1z',
    trash: 'M9 3h6l1 1.5h4.5V7h-17V4.5H8zM5 8.5h14l-1.2 12.5H6.2zM9 11v7.5h1.8V11zm4.2 0v7.5H15V11z',
    play: 'M8 5v14l11-7z',
    pause: 'M6 5h4v14H6zM14 5h4v14h-4z',
    levels: 'M4 13h4v8H4zM10 7h4v14h-4zM16 3h4v18h-4z',
    back: 'M15.4 4.6L8 12l7.4 7.4 1.8-1.8L11.6 12l5.6-5.6z',
    chevron: 'M8.6 5.4L10.4 3.6 18.8 12l-8.4 8.4-1.8-1.8L15.2 12z',
    down: 'M5.4 8.6L3.6 10.4 12 18.8l8.4-8.4-1.8-1.8L12 15.2z',
    chevrons: 'M12 11.2l-6.3-6.3-1.7 1.7 8 8 8-8-1.7-1.7zm0 6.2l-6.3-6.3-1.7 1.7 8 8 8-8-1.7-1.7z',
    menu: 'M4 6h16v2.4H4zm0 4.8h16v2.4H4zm0 4.8h16V18H4z',
    close: 'M6.3 4.5L4.5 6.3 10.2 12l-5.7 5.7 1.8 1.8 5.7-5.7 5.7 5.7 1.8-1.8-5.7-5.7 5.7-5.7-1.8-1.8-5.7 5.7z',
    check: 'M9 15.6L4.9 11.5 3 13.4l6 6L21 7.4l-1.9-1.9z',
    help: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zm-.1 4.5c2.2 0 3.8 1.3 3.8 3.2 0 1.5-.9 2.3-1.9 3-.7.5-.9.8-.9 1.6v.4h-2.2v-.6c0-1.3.5-2 1.5-2.7.8-.6 1.2-1 1.2-1.6 0-.8-.6-1.3-1.5-1.3-1 0-1.6.6-1.7 1.5H8c.1-2.1 1.6-3.5 3.9-3.5zM10.8 15.8h2.4v2.3h-2.4z',
    info: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zM10.8 10h2.4v7.5h-2.4zm0-4h2.4v2.4h-2.4z',
    warn: 'M12 2.5L1 21.5h22zM10.8 9.5h2.4v6h-2.4zm0 7.5h2.4v2.4h-2.4z',
    ok: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zM10.5 16.6l-4.3-4.3 1.7-1.7 2.6 2.6 5.6-5.6 1.7 1.7z',
    error: 'M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20zM8.4 6.7L6.7 8.4 10.3 12l-3.6 3.6 1.7 1.7 3.6-3.6 3.6 3.6 1.7-1.7-3.6-3.6 3.6-3.6-1.7-1.7-3.6 3.6z',
    'eye': 'M12 5C6.5 5 2.5 9.3 1 12c1.5 2.7 5.5 7 11 7s9.5-4.3 11-7c-1.5-2.7-5.5-7-11-7zm0 3.2a3.8 3.8 0 1 1 0 7.6 3.8 3.8 0 0 1 0-7.6zm0 2a1.8 1.8 0 1 0 0 3.6 1.8 1.8 0 0 0 0-3.6z',
    'eye-off': 'M12 5C6.5 5 2.5 9.3 1 12c1.5 2.7 5.5 7 11 7s9.5-4.3 11-7c-1.5-2.7-5.5-7-11-7zm0 3.2a3.8 3.8 0 1 1 0 7.6 3.8 3.8 0 0 1 0-7.6zm0 2a1.8 1.8 0 1 0 0 3.6 1.8 1.8 0 0 0 0-3.6zM2.22 3.78 20.22 21.78 21.78 20.22 3.78 2.22Z',
    'attach': 'M6.5 7.5A5 5 0 0 1 16.5 7.5V16.5A5 5 0 0 1 6.5 16.5ZM8.3 7.5A3.2 3.2 0 0 1 14.7 7.5V16.5A3.2 3.2 0 0 1 8.3 16.5ZM9.6 8.4A1.9 1.9 0 0 1 13.4 8.4V14.6A1.9 1.9 0 0 1 9.6 14.6ZM11.4 8.4A0.1 0.1 0 0 1 11.6 8.4V14.6A0.1 0.1 0 0 1 11.4 14.6Z',
    'send': 'M2.5 20.5l19-8.5-19-8.5.02 6.6L14 12 2.52 13.9z',
    'more': 'M12 2.9a2.1 2.1 0 1 0 0 4.2a2.1 2.1 0 1 0 0 -4.2zM12 9.9a2.1 2.1 0 1 0 0 4.2a2.1 2.1 0 1 0 0 -4.2zM12 16.9a2.1 2.1 0 1 0 0 4.2a2.1 2.1 0 1 0 0 -4.2z',
    'heart': 'M12 21s-7.5-4.6-9.6-9.3C.9 8.2 3 4.5 6.6 4.5c2.2 0 3.6 1.2 5.4 3.2 1.8-2 3.2-3.2 5.4-3.2 3.6 0 5.7 3.7 4.2 7.2C19.5 16.4 12 21 12 21z',
    'share': 'M18 2.2a2.8 2.8 0 1 0 0 5.6a2.8 2.8 0 1 0 0 -5.6zM6 9.2a2.8 2.8 0 1 0 0 5.6a2.8 2.8 0 1 0 0 -5.6zM18 16.2a2.8 2.8 0 1 0 0 5.6a2.8 2.8 0 1 0 0 -5.6zM9.33 11.03 16.03 7.13 15.17 5.67 8.47 9.57ZM8.47 14.43 15.17 18.33 16.03 16.87 9.33 12.97Z',
    'key': 'M7.5 7a5 5 0 1 0 0 10a5 5 0 1 0 0 -10zM7.5 10a2 2 0 1 0 0 4a2 2 0 1 0 0 -4zM12.6 11h9.4v2.4h-2V17h-2.4v-3.6H16V16h-2.4v-2.6h-1z',
    'mail': 'M2.5 5h19v14h-19zM4.3 6.8v10.4h15.4V6.8zM4.3 6.8h2.3L12 11l5.4-4.2h2.3v.4L12 13.2 4.3 7.2z'
  };

  function Icon(p) {
    var s = p.size || 24;
    return h('svg', {
      className: cx('og-icon', p.className), width: s, height: s, viewBox: '0 0 24 24', fill: 'currentColor', fillRule: 'evenodd',
      role: p.label ? 'img' : undefined, 'aria-label': p.label || undefined, 'aria-hidden': p.label ? undefined : true
    }, h('path', { d: PATHS[p.name] || '' }));
  }
  Icon.names = Object.keys(PATHS);

  /* ── actions ── */
  function Button(p) {
    var v = p.variant || 'secondary', size = p.size || 'md';
    var rest = omit(p, ['variant', 'size', 'icon', 'className', 'children', 'block']);
    var iconOnly = p.icon && !p.children;
    return h('button', Object.assign({ type: 'button' }, rest, {
      onPointerDown: pressOn(p.onPointerDown), className: cx('og-btn', 'og-gloss', 'og-press', 'og-btn--' + v, size !== 'md' && 'og-btn--' + size, p.block && 'og-btn--block', iconOnly && 'og-btn--icon', p.className)
    }), p.icon && h(Icon, { name: p.icon, size: size === 'lg' ? 24 : 20 }), p.children && h('span', null, p.children));
  }

  function OrbButton(p) {
    var size = p.size || 'md', tone = p.tone || 'bezel';
    var rest = omit(p, ['size', 'tone', 'icon', 'label', 'className']);
    var isz = size === 'sm' ? 16 : size === 'lg' ? 30 : 24;
    return h('button', Object.assign({ type: 'button', 'aria-label': p.label }, rest, { onPointerDown: pressOn(p.onPointerDown, '.og-orb__core'), className: cx('og-orb', 'og-orb--' + size, 'og-orb--' + tone, p.className) }),
      h('span', { className: 'og-orb__rim' }, h('span', { className: 'og-orb__core' }, h(Icon, { name: p.icon, size: isz }))));
  }

  function Segmented(p) {
    return h('div', { className: cx('og-seg', p.className), role: 'radiogroup', 'aria-label': p.label },
      (p.options || []).map(function (o) {
        var on = o.id === p.value;
        return h('button', { key: o.id, type: 'button', role: 'radio', 'aria-checked': on ? 'true' : 'false', className: 'og-seg__opt',
          onClick: function () { p.onChange && p.onChange(o.id); } }, o.icon && h(Icon, { name: o.icon, size: 18 }), o.label);
      }));
  }

  /* ── navigation ── */
  function WindowBar(p) {
    return h('header', { className: cx(p.overlay ? 'og-winbar--overlay' : 'og-bezel', 'og-winbar', (p.square || p.overlay) && 'og-winbar--square', p.className) },
      p.onBack && h('div', { className: 'og-winbar__lead' }, h(OrbButton, { size: 'sm', icon: 'back', label: p.backLabel || 'Назад', onClick: p.onBack })),
      h('div', { className: 'og-winbar__brand' },
        p.lead ? p.lead : !p.onBack && p.icon && h(Icon, { name: p.icon, size: 24 }),
        h('div', { style: { minWidth: 0 } }, h('h1', { className: 'og-winbar__title' }, p.title), p.subtitle && h('span', { className: 'og-winbar__sub' }, p.subtitle))),
      p.center && h('div', { className: 'og-winbar__center' }, p.center),
      h('div', { className: 'og-winbar__actions' }, (p.actions || []).map(function (a, i) {
        return h(OrbButton, { key: i, size: 'sm', tone: a.tone || 'bezel', icon: a.icon, label: a.label, onClick: a.onPress });
      })));
  }

  function CategoryTabs(p) {
    return h('div', { className: cx('og-cats', p.className), role: 'tablist', 'aria-label': p.label || 'Категории' },
      (p.items || []).map(function (it) {
        var on = it.id === p.value;
        return h('button', { key: it.id, type: 'button', role: 'tab', 'aria-selected': on ? 'true' : 'false', 'aria-label': on ? undefined : it.label, className: 'og-cat',
          onClick: function () { p.onChange && p.onChange(it.id); } },
          h(Icon, { name: it.icon, size: 36, className: 'og-cat__glyph' }),
          on && h('span', { className: 'og-cat__label' }, it.label));
      }));
  }

  function BottomNav(p) {
    return h('nav', { className: cx('og-bezel', 'og-nav', p.square && 'og-nav--square', p.className), 'aria-label': p.label || 'Разделы' },
      (p.items || []).map(function (it) {
        var on = it.id === p.value;
        return h('button', { key: it.id, type: 'button', className: 'og-nav__item og-press', onPointerDown: pressOn(null), 'aria-current': on ? 'page' : undefined,
          onClick: function () { p.onChange && p.onChange(it.id); } },
          h('span', { className: 'og-nav__icon' }, h(Icon, { name: it.icon, size: 22 })), h('span', { className: 'og-nav__label' }, it.label));
      }));
  }

  /* ── containers ── */
  function Panel(p) {
    return h('section', { className: cx('og-panel', p.sunken && 'og-panel--sunken', p.className) },
      p.title && h('h2', { className: 'og-panel__title' }, p.title),
      h('div', { className: cx('og-panel__body', p.flush && 'og-panel__body--flush') }, p.children));
  }

  function Accordion(p) {
    var id = useId('acc');
    var st = R.useState(p.defaultOpen !== false), open = p.open !== undefined ? p.open : st[0];
    function toggle() { if (p.open === undefined) st[1](!open); p.onToggle && p.onToggle(!open); }
    return h('div', { className: cx('og-acc', p.className) },
      h('button', { type: 'button', className: 'og-acc__head', 'aria-expanded': open ? 'true' : 'false', 'aria-controls': id, onClick: toggle },
        h('span', { className: 'og-acc__badge' }, h('span', null, h(Icon, { name: p.icon || 'star', size: 16 }))),
        h('span', { className: 'og-acc__title' }, p.title),
        h('span', { className: 'og-acc__chev' }, h(Icon, { name: 'chevrons', size: 16 }))),
      h('div', { id: id, className: 'og-acc__body', hidden: !open }, p.children));
  }

  function List(p) {
    return h('ul', { className: cx('og-list', p.striped && 'og-list--striped', p.bare && 'og-list--bare', p.className), 'aria-label': p.label },
      R.Children.map(p.children, function (c) { return c ? h('li', null, c) : null; }));
  }

  function ListItem(p) {
    var interactive = !!p.onPress || p.chevron;
    var tag = interactive ? 'button' : 'div';
    var props = { className: cx('og-item', p.dense && 'og-item--dense', p.selected && 'og-item--selected', p.className) };
    if (interactive) { props.type = 'button'; props.onClick = p.onPress; props.onPointerDown = pressOn(null); props.className += ' og-press'; }
    if (p.selected) props['aria-current'] = 'true';
    return h(tag, props,
      p.icon && h('span', { className: 'og-item__lead' }, typeof p.icon === 'string' ? h(Icon, { name: p.icon, size: 22 }) : p.icon),
      h('span', { className: 'og-item__text' }, h('span', { className: 'og-item__title' }, p.title), p.subtitle && h('span', { className: 'og-item__sub' }, p.subtitle)),
      p.value && h('span', { className: 'og-item__value' }, p.value),
      p.trailing,
      p.chevron && h(Icon, { name: 'chevron', size: 18, className: 'og-item__chev' }));
  }

  function ActionTile(p) {
    return h('button', { type: 'button', className: cx('og-tile', 'og-press', p.layout === 'stack' && 'og-tile--stack', p.className), onClick: p.onPress, onPointerDown: pressOn(null) },
      h('span', { className: cx('og-tile__badge', 'og-tile__badge--' + (p.tone || 'chrome')) }, h(Icon, { name: p.icon || 'star', size: 28 })),
      h('span', { className: 'og-tile__text' }, h('span', { className: 'og-tile__title' }, p.title), p.description && h('span', { className: 'og-tile__desc' }, p.description)));
  }

  function Dialog(p) {
    var tid = useId('dlg');
    if (p.open === false) return null;
    var win = h('div', { className: cx('og-bezel', 'og-dialog', p.className), role: p.modal ? 'dialog' : 'group', 'aria-modal': p.modal ? 'true' : undefined, 'aria-labelledby': tid },
      h('div', { className: 'og-dialog__bar' },
        p.icon && h(Icon, { name: p.icon, size: 22 }),
        h('h2', { id: tid, className: 'og-dialog__title' }, p.title),
        p.onClose && h(OrbButton, { size: 'sm', icon: 'close', label: 'Закрыть', onClick: p.onClose })),
      h('div', { className: 'og-dialog__body' },
        typeof p.children === 'string' ? h('p', { className: 'og-dialog__text' }, p.children) : p.children,
        p.actions && h('div', { className: 'og-dialog__actions' }, p.actions)));
    return p.modal ? h('div', { className: 'og-scrim' }, win) : win;
  }

  /* ── forms ── */
  function TextField(p) {
    var id = useId('tf'), hid = id + '-h';
    var rest = omit(p, ['label', 'help', 'error', 'icon', 'className', 'onChange', 'reveal', 'multiline', 'rows']);
    var shown = R.useState(false);
    var type = p.reveal ? (shown[0] ? 'text' : 'password') : (p.type || 'text');
    var fieldProps = Object.assign({ type: type }, rest, { type: p.multiline ? undefined : type, id: id, className: 'og-field__input', 'aria-invalid': p.error ? 'true' : undefined,
      'aria-describedby': (p.error || p.help) ? hid : undefined, rows: p.multiline ? (p.rows || 3) : undefined,
      onChange: function (e) { p.onChange && p.onChange(e.target.value, e); } });
    return h('div', { className: cx('og-field', p.error && 'og-field--error', p.disabled && 'og-field--disabled', p.multiline && 'og-field--multi', p.className) },
      p.label && h('label', { className: 'og-field__label', htmlFor: id }, p.label),
      h('div', { className: 'og-field__box' },
        p.icon && h(Icon, { name: p.icon, size: 20 }),
        h(p.multiline ? 'textarea' : 'input', fieldProps),
        p.reveal && h(OrbButton, { size: 'sm', tone: 'chrome', icon: shown[0] ? 'eye-off' : 'eye', label: shown[0] ? 'Скрыть пароль' : 'Показать пароль', 'aria-pressed': shown[0] ? 'true' : 'false', onClick: function () { shown[1](!shown[0]); }, className: 'og-field__reveal' })),
      (p.error || p.help) && h('div', { id: hid, className: 'og-field__help' }, p.error && h(Icon, { name: 'error', size: 14 }), typeof p.error === 'string' ? p.error : p.help));
  }

  function Select(p) {
    var id = useId('sel');
    var opts = p.options || [];
    var cur = opts.filter(function (o) { return String(o.value) === String(p.value); })[0] || opts[0] || { label: '' };
    return h('div', { className: cx('og-field', p.className) },
      p.label && h('label', { className: 'og-field__label', htmlFor: id }, p.label),
      h('div', { className: 'og-select' },
        h('span', { className: 'og-select__value', 'aria-hidden': true }, cur.icon && h(Icon, { name: cur.icon, size: 18 }), cur.label),
        h('span', { className: 'og-select__arrow', 'aria-hidden': true }, h(Icon, { name: 'down', size: 16 })),
        h('select', { id: id, className: 'og-select__native', value: p.value, disabled: p.disabled, 'aria-label': p.label ? undefined : (p.ariaLabel || 'Выбор'),
          onChange: function (e) { p.onChange && p.onChange(e.target.value, e); } },
          opts.map(function (o) { return h('option', { key: o.value, value: o.value }, o.label); }))));
  }

  function Checkbox(p) {
    var rest = omit(p, ['label', 'className', 'onChange']);
    return h('label', { className: cx('og-check', p.disabled && 'og-check--disabled', p.className) },
      h('input', Object.assign({ type: 'checkbox' }, rest, { className: 'og-check__input', onChange: function (e) { p.onChange && p.onChange(e.target.checked, e); } })),
      h('span', { className: 'og-check__box', 'aria-hidden': true }, h(Icon, { name: 'check', size: 16 })),
      h('span', null, p.label));
  }

  function RadioGroup(p) {
    var name = useId('rg');
    return h('fieldset', { className: cx('og-radios', p.className) },
      p.label && h('legend', { className: 'og-radios__legend' }, p.label),
      (p.options || []).map(function (o) {
        return h('label', { key: o.value, className: cx('og-check', o.disabled && 'og-check--disabled') },
          h('input', { type: 'radio', name: p.name || name, value: o.value, className: 'og-check__input', disabled: o.disabled,
            checked: p.value !== undefined ? String(p.value) === String(o.value) : undefined,
            defaultChecked: p.value === undefined ? String(p.defaultValue) === String(o.value) : undefined,
            onChange: function () { p.onChange && p.onChange(o.value); } }),
          h('span', { className: 'og-check__box og-check__box--radio', 'aria-hidden': true }, h('span', { className: 'og-check__dot' })),
          h('span', null, o.label));
      }));
  }

  function Switch(p) {
    var rest = omit(p, ['label', 'hint', 'className', 'onChange']);
    var st = R.useState(!!(p.checked !== undefined ? p.checked : p.defaultChecked));
    var on = p.checked !== undefined ? p.checked : st[0];
    return h('label', { className: cx('og-switch', p.className) },
      h('span', { className: 'og-switch__text' }, h('span', null, p.label), p.hint && h('span', { className: 'og-switch__hint' }, p.hint)),
      h('input', Object.assign({ type: 'checkbox', role: 'switch' }, rest, { className: 'og-switch__input', 'aria-checked': on ? 'true' : 'false',
        onChange: function (e) { st[1](e.target.checked); p.onChange && p.onChange(e.target.checked, e); } })),
      h('span', { className: 'og-switch__track', 'aria-hidden': true }, h('span', { className: 'og-switch__led' }, on ? 'ON' : 'OFF'), h('span', { className: 'og-switch__knob' })));
  }

  function Slider(p) {
    var v = clamp01(p.value);
    return h('div', { className: cx('og-slider', p.className) },
      (p.label || p.valueText) && h('div', { className: 'og-slider__head', 'aria-hidden': true },
        h('span', { className: 'og-slider__label' }, p.label), h('span', { className: 'og-slider__value' }, p.valueText)),
      h('input', { type: 'range', min: 0, max: 1000, value: Math.round(v * 1000), className: 'og-slider__input', disabled: p.disabled,
        'aria-label': p.label || 'Значение', 'aria-valuetext': p.valueText,
        style: { '--og-fill': (v * 100).toFixed(1) + '%' },
        onChange: function (e) { p.onChange && p.onChange(Number(e.target.value) / 1000); } }));
  }

  /* ── feedback ── */
  function ProgressBar(p) {
    var ind = p.value === undefined || p.value === null, pct = ind ? null : Math.round(clamp01(p.value) * 100), status = p.status;
    var detail = status === 'done' ? h('div', { className: 'og-progress__detail og-progress__detail--done' }, h(Icon, { name: 'ok', size: 16 }), p.detail || 'Готово')
      : status === 'error' ? h('div', { className: 'og-progress__detail og-progress__detail--error' }, h(Icon, { name: 'error', size: 16 }), p.detail || 'Ошибка')
      : p.detail && h('div', { className: 'og-progress__detail' }, p.detail);
    return h('div', { className: cx('og-progress', ind && 'og-progress--indeterminate', p.className) },
      (p.label || !ind) && h('div', { className: 'og-progress__head' }, h('span', { className: 'og-progress__label' }, p.label), !ind && h('span', { className: 'og-progress__pct' }, pct + '%')),
      h('div', { className: 'og-progress__well', role: 'progressbar', 'aria-valuemin': 0, 'aria-valuemax': 100, 'aria-valuenow': ind ? undefined : pct, 'aria-label': p.label || 'Выполнение' },
        h('div', { className: 'og-progress__fill', style: ind ? null : { width: pct + '%' } })),
      detail);
  }

  function Meter(p) {
    var v = clamp01(p.value), n = p.segments || 20, lit = Math.round(v * n), segs = [];
    for (var i = 0; i < n; i++) {
      var f = (i + 1) / n, tone = f > 0.85 ? 'peak' : f > 0.6 ? 'mid' : 'low';
      segs.push(h('span', { key: i, style: { '--i': i }, className: cx('og-meter__seg', i < lit && 'og-meter__seg--' + tone) }));
    }
    return h('div', { className: cx('og-lcd', 'og-meter', p.className), role: 'meter', 'aria-valuemin': 0, 'aria-valuemax': 100, 'aria-valuenow': Math.round(v * 100), 'aria-valuetext': p.valueText, 'aria-label': p.label },
      h('div', { className: 'og-meter__head' }, h('span', { className: 'og-meter__label' }, p.label), p.valueText && h('span', { className: 'og-meter__val' }, p.valueText)),
      h('div', { className: 'og-meter__bar', 'aria-hidden': true }, segs));
  }

  function Readout(p) {
    var val = String(p.value == null ? '' : p.value);
    return h('div', { className: cx('og-lcd', 'og-readout', p.size === 'sm' && 'og-readout--sm', p.className), role: 'group', 'aria-label': [p.label, val, p.unit].filter(Boolean).join(' ') },
      p.label && h('span', { className: 'og-readout__tag', 'aria-hidden': true }, p.label),
      h('span', { className: 'og-readout__row', 'aria-hidden': true },
        h('span', { className: 'og-readout__val' }, h('span', { className: 'og-readout__ghost' }, val.replace(/[0-9]/g, '8')), h('span', { key: val, className: 'og-readout__digits' }, val)),
        p.unit && h('span', { className: 'og-readout__unit' }, p.unit)));
  }

  function Badge(p) {
    var tone = p.tone || 'neutral';
    var icon = tone === 'success' ? 'ok' : tone === 'warning' ? 'warn' : tone === 'danger' ? 'error' : null;
    return h('span', { className: cx('og-badge', 'og-badge--' + tone, p.count && 'og-badge--count', p.className) },
      icon && !p.count && h(Icon, { name: icon, size: 14 }), p.count ? h('span', { key: String(p.children), className: 'og-badge__n' }, p.children) : p.children);
  }

  function Balloon(p) {
    return h('div', { className: cx('og-balloon', p.tail === 'top' && 'og-balloon--top', p.tail === 'none' && 'og-balloon--none', p.className), role: 'status' },
      h('span', { className: 'og-balloon__mark' }, h(Icon, { name: p.icon || 'info', size: 20 })),
      h('div', null, p.title && h('p', { className: 'og-balloon__title' }, p.title), p.children && h('p', { className: 'og-balloon__body' }, p.children)),
      p.onClose ? h('button', { type: 'button', className: 'og-balloon__close', onClick: p.onClose, 'aria-label': 'Закрыть' }, h(Icon, { name: 'close', size: 16 })) : h('span'));
  }

  /* ════ Material-style additions ════ */
  function useOutside(open, close, ref) {
    R.useEffect(function () {
      if (!open) return;
      function down(e) { if (ref.current && !ref.current.contains(e.target)) close(); }
      function key(e) { if (e.key === 'Escape') close(); }
      document.addEventListener('pointerdown', down); document.addEventListener('keydown', key);
      return function () { document.removeEventListener('pointerdown', down); document.removeEventListener('keydown', key); };
    }, [open]);
  }

  function Card(p) {
    var tag = p.onPress ? 'button' : 'article';
    var media = p.image ? h('img', { src: p.image, alt: p.imageAlt || '' }) : p.media && typeof p.media === 'string' ? h(Icon, { name: p.media, size: 56 }) : p.media;
    var props = { className: cx('og-card', p.variant === 'sunken' && 'og-card--sunken', p.onPress && 'og-press', p.className) };
    if (p.onPress) { props.type = 'button'; props.onClick = p.onPress; props.onPointerDown = pressOn(null); }
    return h(tag, props,
      p.bar && h('div', { className: 'og-bezel og-card__bar' }, p.barIcon && h(Icon, { name: p.barIcon, size: 18 }), p.bar),
      media && h('div', { className: 'og-card__media' }, media),
      (p.title || p.subtitle || p.children) && h('div', { className: 'og-card__body' },
        p.title && h('h3', { className: 'og-card__title' }, p.title),
        p.subtitle && h('span', { className: 'og-card__sub' }, p.subtitle),
        p.children && h('div', { className: 'og-card__text' }, p.children)),
      p.actions && h('div', { className: 'og-card__actions' }, p.actions));
  }

  function Chip(p) {
    var kind = p.kind || 'assist';
    if (kind === 'input') {
      return h('span', { className: cx('og-chip', p.className) },
        p.icon && h(Icon, { name: p.icon, size: 16 }), p.children,
        h('button', { type: 'button', className: 'og-chip__x', 'aria-label': 'Убрать ' + (typeof p.children === 'string' ? p.children : ''),
          onClick: function () { p.onRemove && p.onRemove(); } }, h(Icon, { name: 'close', size: 12 })));
    }
    var props = { type: 'button', className: cx('og-chip', p.className), disabled: p.disabled, onClick: p.onPress, onPointerDown: pressOn(null, '.og-chip__fx') };
    if (kind === 'filter') props['aria-pressed'] = p.selected ? 'true' : 'false';
    return h('button', props, h('span', { className: 'og-chip__fx', 'aria-hidden': true }),
      kind === 'filter' && p.selected ? h(Icon, { key: 'c', name: 'check', size: 16, className: 'og-chip__check' }) : p.icon && h(Icon, { key: 'i', name: p.icon, size: 16 }),
      p.children);
  }
  function ChipGroup(p) { return h('div', { className: cx('og-chips', p.scroll && 'og-chips--scroll', p.className), role: 'group', 'aria-label': p.label }, p.children); }

  function Fab(p) {
    var rest = omit(p, ['icon', 'label', 'tone', 'className', 'expanded', 'docked']);
    var el = h('button', Object.assign({ type: 'button', 'aria-label': p.label ? undefined : (p.ariaLabel || 'Действие') }, rest, {
      'aria-expanded': p.expanded === undefined ? undefined : (p.expanded ? 'true' : 'false'),
      onPointerDown: pressOn(p.onPointerDown),
      className: cx('og-fab', 'og-press', !p.label && 'og-fab--icon', p.tone === 'chrome' && 'og-fab--chrome', p.className) }),
      h(Icon, { name: p.icon || 'plus', size: 26 }), p.label && h('span', null, p.label));
    return p.docked ? h('div', { className: 'og-fab-dock' }, el) : el;
  }

  function Menu(p) {
    var st = R.useState(!!p.defaultOpen), open = st[0], ref = R.useRef(null);
    function close() { st[1](false); }
    useOutside(open, close, ref);
    var n = 0;
    var trigger = p.trigger ? R.cloneElement(p.trigger, { onClick: function () { st[1](!open); }, 'aria-haspopup': 'menu', 'aria-expanded': open ? 'true' : 'false' }) : null;
    return h('div', { className: 'og-menu-anchor', ref: ref }, trigger,
      open && h('div', { className: cx('og-menu', p.align === 'end' && 'og-menu--end'), role: 'menu', 'aria-label': p.label },
        (p.items || []).map(function (it, i) {
          if (it.divider) return h('div', { key: 'd' + i, className: 'og-menu__sep', role: 'separator' });
          var k = n++;
          return h('button', { key: it.id, type: 'button', role: 'menuitem', style: { '--i': k }, className: cx('og-menu__item', it.danger && 'og-menu__item--danger'),
            onClick: function () { close(); p.onSelect && p.onSelect(it.id); } },
            h('span', { className: 'og-menu__icon' }, it.icon && h(Icon, { name: it.icon, size: 18 })), h('span', null, it.label), it.hint ? h('span', { className: 'og-menu__hint' }, it.hint) : h('span'));
        })));
  }

  function BottomSheet(p) {
    var tid = useId('sh');
    if (p.open === false) return null;
    var sheet = h('div', { className: cx('og-bezel', 'og-sheet', p.inline && 'og-sheet--inline', p.className), role: p.inline ? 'region' : 'dialog', 'aria-modal': p.inline ? undefined : 'true', 'aria-labelledby': tid },
      h('span', { className: 'og-sheet__grab', 'aria-hidden': true }),
      h('div', { className: 'og-sheet__bar' }, h('h2', { id: tid, className: 'og-sheet__title' }, p.title), p.onClose && h(OrbButton, { size: 'sm', icon: 'close', label: 'Закрыть', onClick: p.onClose })),
      h('div', { className: 'og-sheet__body' }, p.children));
    if (p.inline) return sheet;
    return h(R.Fragment, null, h('div', { className: 'og-sheet-scrim', onClick: p.onClose }), sheet);
  }

  function NavDrawer(p) {
    if (p.open === false) return null;
    var k = 0;
    var drawer = h('nav', { className: cx('og-drawer', p.inline && 'og-drawer--inline', p.className), 'aria-label': p.label || 'Меню' },
      h('div', { className: 'og-bezel og-drawer__head' },
        p.avatar,
        h('div', { className: 'og-drawer__who' }, h('span', { className: 'og-drawer__name' }, p.title), p.subtitle && h('span', { className: 'og-drawer__mail' }, p.subtitle))),
      h('ul', { className: 'og-drawer__list' }, (p.items || []).map(function (it, i) {
        if (it.section) return h('li', { key: 's' + i, className: 'og-drawer__section' }, it.section);
        var on = it.id === p.value, idx = k++;
        return h('li', { key: it.id }, h('button', { type: 'button', className: 'og-drawer__item og-press', style: { '--i': idx }, 'aria-current': on ? 'page' : undefined,
          onPointerDown: pressOn(null), onClick: function () { p.onChange && p.onChange(it.id); } },
          h(Icon, { name: it.icon, size: 22 }), h('span', { className: 'og-drawer__label' }, it.label), it.badge && h(Badge, { tone: 'accent', count: true }, it.badge)));
      })));
    if (p.inline) return drawer;
    return h(R.Fragment, null, h('div', { className: 'og-drawer-scrim', onClick: p.onClose }), drawer);
  }

  function Tabs(p) {
    var base = useId('tabs');
    var items = p.items || [];
    return h('div', { className: cx('og-tabs', p.className) },
      h('div', { className: 'og-tabs__list', role: 'tablist', 'aria-label': p.label },
        items.map(function (it) {
          var on = it.id === p.value;
          return h('button', { key: it.id, id: base + '-' + it.id, type: 'button', role: 'tab', 'aria-selected': on ? 'true' : 'false', 'aria-controls': base + '-panel', tabIndex: on ? 0 : -1, className: 'og-tab',
            onClick: function () { p.onChange && p.onChange(it.id); } },
            it.icon && h(Icon, { name: it.icon, size: 16 }), it.label, it.badge && h(Badge, { tone: 'accent', count: true }, it.badge));
        })),
      p.children !== undefined && h('div', { key: p.value, id: base + '-panel', role: 'tabpanel', 'aria-labelledby': base + '-' + p.value, className: 'og-tabs__panel' }, p.children));
  }

  function Snackbar(p) {
    R.useEffect(function () {
      if (p.open === false || !p.duration || !p.onClose) return;
      var t = setTimeout(p.onClose, p.duration); return function () { clearTimeout(t); };
    }, [p.open, p.duration]);
    if (p.open === false) return null;
    return h('div', { className: cx('og-bezel', 'og-snack', p.inline && 'og-snack--inline', p.className), role: 'status', 'aria-live': 'polite' },
      h('span', { className: 'og-snack__msg' }, p.children),
      p.actionLabel && h(Button, { size: 'sm', onClick: p.onAction }, p.actionLabel),
      p.onClose && h(OrbButton, { size: 'sm', icon: 'close', label: 'Закрыть', onClick: p.onClose }));
  }

  function Tooltip(p) {
    var id = useId('tip'), st = R.useState(!!p.defaultOpen), open = st[0], timer = R.useRef(null);
    function show() { clearTimeout(timer.current); st[1](true); }
    function hide() { clearTimeout(timer.current); st[1](false); }
    var child = R.cloneElement(p.children, { 'aria-describedby': id });
    return h('span', { className: 'og-tip-anchor', onMouseEnter: show, onMouseLeave: hide, onFocus: show, onBlur: hide,
      onTouchStart: function () { timer.current = setTimeout(show, 450); },
      onTouchEnd: function () { clearTimeout(timer.current); timer.current = setTimeout(hide, 1500); } },
      child,
      h('span', { id: id, role: 'tooltip', className: cx('og-tip', p.placement === 'below' && 'og-tip--below'), hidden: !open }, open && h('span', { className: 'og-tip__in' }, p.label)));
  }

  function SearchBar(p) {
    var st = R.useState(p.defaultValue || ''), val = p.value !== undefined ? p.value : st[0];
    function set(v) { if (p.value === undefined) st[1](v); p.onChange && p.onChange(v); }
    return h('div', { className: cx('og-search', p.className), role: 'search' },
      h(Icon, { name: 'search', size: 20, className: 'og-search__glass' }),
      h('input', { type: 'search', className: 'og-search__input', value: val, placeholder: p.placeholder || 'Поиск', 'aria-label': p.label || p.placeholder || 'Поиск',
        onChange: function (e) { set(e.target.value); }, onKeyDown: function (e) { if (e.key === 'Enter' && p.onSubmit) p.onSubmit(val); } }),
      h('div', { className: 'og-search__end' },
        val && h(OrbButton, { key: 'x', size: 'sm', tone: 'chrome', icon: 'close', label: 'Очистить', onClick: function () { set(''); } }),
        p.trailing));
  }

  function Spinner(p) {
    return h('div', { className: cx('og-spinner', p.size && 'og-spinner--' + p.size, p.className), role: 'progressbar', 'aria-label': p.label || 'Загрузка' },
      h('span', { className: 'og-spinner__disc', 'aria-hidden': true }), p.label && h('span', { className: 'og-spinner__label', 'aria-hidden': true }, p.label));
  }

  function Divider(p) {
    return p.label ? h('div', { className: cx('og-divider', 'og-divider--label', p.className), role: 'separator' }, p.label)
      : h('hr', { className: cx('og-divider', p.className) });
  }

  function Avatar(p) {
    var initials = (p.name || '').split(/\s+/).filter(Boolean).slice(0, 2).map(function (w) { return w[0]; }).join('').toUpperCase();
    var st = { online: 'в сети', busy: 'занят', away: 'отошёл' }[p.status];
    return h('span', { className: cx('og-avatar', p.size && p.size !== 'md' && 'og-avatar--' + p.size, p.className), role: 'img', 'aria-label': [p.name, st].filter(Boolean).join(', ') },
      h('span', { className: 'og-avatar__face', 'aria-hidden': true }, p.src ? h('img', { src: p.src, alt: '' }) : p.icon ? h(Icon, { name: p.icon, size: 20 }) : initials),
      p.status && h('span', { className: 'og-avatar__led og-avatar__led--' + p.status, 'aria-hidden': true }));
  }

  function Stepper(p) {
    var cur = p.current || 0;
    return h('ol', { className: cx('og-stepper', p.className), 'aria-label': p.label || 'Шаги' },
      (p.steps || []).map(function (s, i) {
        var state = i < cur ? 'done' : i === cur ? 'current' : 'todo';
        return h('li', { key: i, className: cx('og-step', 'og-step--' + state), 'aria-current': state === 'current' ? 'step' : undefined },
          h('span', { className: 'og-step__orb' }, h('span', { className: 'og-step__core' }, state === 'done' ? h(Icon, { name: 'check', size: 16, label: 'готово' }) : String(i + 1))),
          h('span', null, s));
      }));
  }

  var MONTHS = ['Январь', 'Февраль', 'Март', 'Апрель', 'Май', 'Июнь', 'Июль', 'Август', 'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'];
  var WD = ['ПН', 'ВТ', 'СР', 'ЧТ', 'ПТ', 'СБ', 'ВС'];
  function pad(n) { return (n < 10 ? '0' : '') + n; }
  function iso(y, m, d) { return y + '-' + pad(m + 1) + '-' + pad(d); }
  function DatePicker(p) {
    var today = new Date(), sel = p.value ? p.value.split('-').map(Number) : null;
    var init = sel ? [sel[0], sel[1] - 1] : [today.getFullYear(), today.getMonth()];
    var vm = R.useState({ y: init[0], m: init[1], dir: '' }), v = vm[0];
    function go(d) { var m = v.m + d, y = v.y; if (m < 0) { m = 11; y--; } if (m > 11) { m = 0; y++; } vm[1]({ y: y, m: m, dir: d > 0 ? 'next' : 'prev' }); }
    var first = (new Date(v.y, v.m, 1).getDay() + 6) % 7, days = new Date(v.y, v.m + 1, 0).getDate(), cells = [];
    WD.forEach(function (w) { cells.push(h('span', { key: 'w' + w, className: 'og-date__wd', 'aria-hidden': true }, w)); });
    for (var i = 0; i < first; i++) cells.push(h('span', { key: 'e' + i }));
    var tIso = iso(today.getFullYear(), today.getMonth(), today.getDate());
    for (var d = 1; d <= days; d++) (function (d) {
      var id = iso(v.y, v.m, d);
      cells.push(h('button', { key: id, type: 'button', className: cx('og-date__day', id === tIso && 'og-date__day--today'), 'aria-pressed': id === p.value ? 'true' : 'false',
        'aria-label': d + ' ' + MONTHS[v.m].toLowerCase() + ' ' + v.y, onClick: function () { p.onChange && p.onChange(id); } }, d));
    })(d);
    return h('div', { className: cx('og-date', p.className), role: 'group', 'aria-label': p.label || 'Выбор даты' },
      h('div', { className: 'og-date__head' },
        h(OrbButton, { size: 'sm', tone: 'chrome', icon: 'back', label: 'Предыдущий месяц', onClick: function () { go(-1); } }),
        h('h3', { className: 'og-date__title', 'aria-live': 'polite' }, MONTHS[v.m] + ' ' + v.y),
        h(OrbButton, { size: 'sm', tone: 'chrome', icon: 'chevron', label: 'Следующий месяц', onClick: function () { go(1); } })),
      h('div', { key: v.y + '-' + v.m, className: cx('og-date__grid', v.dir && 'og-date__grid--' + v.dir) }, cells));
  }

  function Banner(p) {
    if (p.open === false) return null;
    return h('div', { className: cx('og-banner', p.boxed && 'og-banner--boxed', p.className), role: 'status' },
      h('span', { className: 'og-banner__mark' }, h(Icon, { name: p.icon || 'info', size: 20 })),
      h('div', null, p.title && h('strong', { className: 'og-banner__title' }, p.title), p.children),
      p.actions && h('div', { className: 'og-banner__actions' }, p.actions));
  }

  var SKEL_W = ['100%', '92%', '78%', '88%', '70%'];
  function Skeleton(p) {
    var v = p.variant || 'text', i0 = p.index || 0;
    function lines(n, from) {
      var out = [];
      for (var i = 0; i < n; i++) out.push(h('span', { key: i, className: 'og-skel og-skel--text', style: { '--i': from + i, width: i === n - 1 && n > 1 ? '58%' : (p.width || SKEL_W[i % SKEL_W.length]) } }));
      return h('span', { className: 'og-skel-group__lines', style: { display: 'block' } }, out);
    }
    function circle(size, i) { return h('span', { className: 'og-skel og-skel--circle', style: { '--i': i, width: size, height: size, flex: 'none' } }); }
    function rect(height, i) {
      return h('span', { className: 'og-skel og-skel--rect', style: { '--i': i, width: p.width || '100%', height: height } }, h(Icon, { name: p.icon || 'image', size: 40, className: 'og-skel__glyph' }));
    }
    var body;
    if (v === 'text') body = lines(p.lines || 3, i0);
    else if (v === 'circle') body = circle(p.width || p.height || 44, i0);
    else if (v === 'rect') body = rect(p.height || 120, i0);
    else if (v === 'row') body = h('span', { className: 'og-skel-group' }, circle(44, i0), lines(p.lines || 2, i0 + 1));
    else if (v === 'card') body = h('span', { className: 'og-skel-card' }, rect(p.height || 120, i0), h('span', { className: 'og-skel-group' }, circle(36, i0 + 1), lines(p.lines || 2, i0 + 2)));
    return h('span', { className: cx(p.className), style: { display: 'block' }, 'aria-hidden': true }, body);
  }

  /* ════ Components found by the stress screens ════ */

  /* One-time code on LCD cells. One real input underneath (paste, autofill, IME work). */
  function CodeInput(p) {
    var n = p.length || 6, id = useId('code');
    var st = R.useState(p.defaultValue || ''), val = (p.value !== undefined ? p.value : st[0]) || '';
    var foc = R.useState(false);
    function set(v) { v = v.replace(/\D/g, '').slice(0, n); if (p.value === undefined) st[1](v); p.onChange && p.onChange(v); if (v.length === n && p.onComplete) p.onComplete(v); }
    var cells = [];
    for (var i = 0; i < n; i++) {
      var ch = val[i], active = foc[0] && (i === val.length || (i === n - 1 && val.length === n));
      cells.push(h('span', { key: i, className: cx('og-code__cell', ch && 'og-code__cell--on', active && 'og-code__cell--active') },
        h('span', { className: 'og-code__ghost' }, '8'), ch && h('span', { key: ch + i, className: 'og-code__digit' }, ch)));
      if (n === 6 && i === 2) cells.push(h('span', { key: 'dash', className: 'og-code__dash' }, '–'));
    }
    return h('div', { className: cx('og-field', 'og-code', p.error && 'og-field--error', p.className) },
      p.label && h('label', { className: 'og-field__label', htmlFor: id }, p.label),
      h('div', { className: 'og-code__row' },
        h('div', { className: 'og-code__cells', 'aria-hidden': true }, cells),
        h('input', { id: id, className: 'og-code__input', value: val, inputMode: 'numeric', autoComplete: 'one-time-code', maxLength: n, 'aria-invalid': p.error ? 'true' : undefined,
          onChange: function (e) { set(e.target.value); }, onFocus: function () { foc[1](true); }, onBlur: function () { foc[1](false); } })),
      (p.error || p.help) && h('div', { className: 'og-field__help' }, p.error && h(Icon, { name: 'error', size: 14 }), typeof p.error === 'string' ? p.error : p.help));
  }

  /* Empty / error / offline states: a big glossy orb, a fact, what to do next. */
  function EmptyState(p) {
    var tone = p.tone || 'empty';
    var icon = p.icon || (tone === 'error' ? 'error' : tone === 'offline' ? 'cloud' : 'folder');
    return h('div', { className: cx('og-empty', 'og-empty--' + tone, p.className), role: tone === 'empty' ? undefined : 'alert' },
      h('span', { className: 'og-empty__orb' }, h('span', { className: 'og-empty__core' }, h(Icon, { name: icon, size: 36 }))),
      h('h3', { className: 'og-empty__title' }, p.title),
      p.children && h('p', { className: 'og-empty__text' }, p.children),
      p.action && h('div', { className: 'og-empty__action' }, p.action));
  }

  /* A titled list section; the title sticks to the top of the scroll container. */
  function ListSection(p) {
    return h('section', { className: cx('og-lsec', p.className), 'aria-label': typeof p.title === 'string' ? p.title : undefined },
      h('div', { className: 'og-lsec__head' }, h('span', { className: 'og-lsec__title' }, p.title), p.meta && h('span', { className: 'og-lsec__meta' }, p.meta)),
      p.children);
  }

  /* Row with actions revealed by swiping left. The actions stay in the tab order for keyboard and screen readers. */
  function SwipeRow(p) {
    var acts = p.actions || [], W = acts.length * 72;
    var st = R.useState(p.defaultOpen ? -W : 0), x = st[0], drag = R.useRef(null), dg = R.useState(false), justDragged = R.useRef(false);
    function down(e) { drag.current = { x0: e.clientX, y0: e.clientY, base: x, on: false }; }
    function move(e) {
      var d = drag.current; if (!d) return;
      var dx = e.clientX - d.x0, dy = e.clientY - d.y0;
      if (!d.on) { if (Math.abs(dx) > 8 && Math.abs(dx) > Math.abs(dy)) { d.on = true; dg[1](true); try { e.currentTarget.setPointerCapture(e.pointerId); } catch (_) {} } else return; }
      var nx = d.base + dx; if (nx > 0) nx = nx / 4; if (nx < -W) nx = -W + (nx + W) / 4;
      st[1](nx);
    }
    function up() { var d = drag.current; drag.current = null; if (!d || !d.on) return; justDragged.current = true; setTimeout(function () { justDragged.current = false; }, 0); dg[1](false); st[1](x < -W / 2 ? -W : 0); }
    return h('div', { className: cx('og-swipe', dg[0] && 'og-swipe--drag', p.className) },
      h('div', { className: 'og-swipe__acts', style: { width: W } }, acts.map(function (a, i) {
        return h('button', { key: i, type: 'button', className: cx('og-swipe__act', 'og-swipe__act--' + (a.tone || 'chrome')), style: { '--i': acts.length - i },
          onClick: function () { st[1](0); a.onPress && a.onPress(); }, onFocus: function () { st[1](-W); } },
          h(Icon, { name: a.icon, size: 20 }), h('span', null, a.label));
      })),
      h('div', { className: 'og-swipe__front', style: { transform: 'translateX(' + x + 'px)' },
        onPointerDown: down, onPointerMove: move, onPointerUp: up, onPointerCancel: up,
        onClickCapture: function (e) {
          if (justDragged.current) { justDragged.current = false; e.stopPropagation(); e.preventDefault(); return; }
          if (x !== 0) { e.stopPropagation(); e.preventDefault(); st[1](0); } } }, p.children));
  }

  /* Pull-to-refresh: a disc drops in from the top, spins with the pull and keeps spinning while refreshing. */
  function PullRefresh(p) {
    var st = R.useState(0), pull = st[0], drag = R.useRef(null), box = R.useRef(null), TH = 64;
    function down(e) { if (box.current && box.current.scrollTop <= 0 && !p.refreshing) drag.current = { y0: e.clientY, on: false }; }
    function move(e) {
      var d = drag.current; if (!d) return;
      var dy = e.clientY - d.y0;
      if (!d.on) { if (dy > 8) { d.on = true; try { e.currentTarget.setPointerCapture(e.pointerId); } catch (_) {} } else return; }
      st[1](Math.max(0, Math.min(120, dy * 0.55)));
    }
    function up() { var d = drag.current; drag.current = null; if (!d || !d.on) return; if (pull >= TH && p.onRefresh) p.onRefresh(); st[1](0); }
    var shown = p.refreshing ? 56 : pull, ready = pull >= TH;
    return h('div', { className: cx('og-pull', p.className) },
      h('div', { className: cx('og-pull__ind', p.refreshing && 'og-pull__ind--busy', ready && 'og-pull__ind--ready'), style: { height: shown, opacity: shown ? 1 : 0 }, 'aria-hidden': !p.refreshing },
        h('span', { className: 'og-pull__disc', style: { transform: 'rotate(' + pull * 4 + 'deg) scale(' + Math.min(1, 0.4 + shown / 90) + ')' } }),
        p.refreshing && h('span', { className: 'og-sr', role: 'status' }, p.label || 'Обновляю')),
      h('div', { ref: box, className: 'og-pull__scroll', style: p.height ? { height: p.height } : null,
        onPointerDown: down, onPointerMove: move, onPointerUp: up, onPointerCancel: up }, p.children));
  }

  /* Chat */
  function ChatBubble(p) {
    var me = p.from === 'me';
    return h('div', { className: cx('og-bubble', me ? 'og-bubble--me' : 'og-bubble--them', p.tail === false && 'og-bubble--notail', p.className) },
      !me && p.avatar && h('span', { className: 'og-bubble__av' }, p.avatar),
      h('div', { className: 'og-bubble__body' },
        p.author && !me && h('span', { className: 'og-bubble__author' }, p.author),
        h('span', { className: 'og-bubble__text' }, p.children),
        (p.time || p.status) && h('span', { className: 'og-bubble__meta' }, p.time,
          me && p.status && h('span', { className: cx('og-bubble__tick', p.status === 'read' && 'og-bubble__tick--read'), role: 'img', 'aria-label': p.status === 'read' ? 'прочитано' : 'отправлено' },
            h(Icon, { name: 'check', size: 14 }), p.status === 'read' && h(Icon, { name: 'check', size: 14 })))));
  }

  function TypingIndicator(p) {
    return h('div', { className: cx('og-bubble', 'og-bubble--them', 'og-typing', p.className), role: 'status', 'aria-label': (p.name ? p.name + ' ' : '') + 'печатает' },
      p.avatar && h('span', { className: 'og-bubble__av' }, p.avatar),
      h('div', { className: 'og-bubble__body' }, h('span', { className: 'og-typing__dots', 'aria-hidden': true }, h('i', { style: { '--i': 0 } }), h('i', { style: { '--i': 1 } }), h('i', { style: { '--i': 2 } }))));
  }

  function Composer(p) {
    var st = R.useState(''), val = p.value !== undefined ? p.value : st[0], ta = R.useRef(null);
    function set(v) { if (p.value === undefined) st[1](v); p.onChange && p.onChange(v); }
    R.useEffect(function () { var t = ta.current; if (!t) return; t.style.height = 'auto'; t.style.height = Math.min(t.scrollHeight, 120) + 'px'; }, [val]);
    function send() { if (!val.trim()) return; p.onSend && p.onSend(val); set(''); }
    return h('div', { className: cx('og-bezel', 'og-composer', p.className) },
      p.onAttach !== false && h(OrbButton, { size: 'sm', tone: 'chrome', icon: 'attach', label: 'Прикрепить', onClick: p.onAttach || undefined }),
      h('div', { className: 'og-composer__box' },
        h('textarea', { ref: ta, rows: 1, className: 'og-composer__input', value: val, placeholder: p.placeholder || 'Сообщение', 'aria-label': p.label || 'Сообщение',
          onChange: function (e) { set(e.target.value); }, onKeyDown: function (e) { if (e.key === 'Enter' && !e.shiftKey) { e.preventDefault(); send(); } } })),
      val.trim() ? h(OrbButton, { key: 'send', tone: 'accent', icon: 'send', label: 'Отправить', onClick: send, className: 'og-composer__send' })
        : h(OrbButton, { key: 'idle', tone: 'chrome', icon: 'send', label: 'Отправить', disabled: true, className: 'og-composer__send' }));
  }

  /* Page indicator for carousels and pagers: LCD capsules, the current one stretched and lit. */
  function PageDots(p) {
    var n = p.count || 0, out = [];
    for (var i = 0; i < n; i++) (function (i) {
      out.push(h('button', { key: i, type: 'button', className: 'og-dots__dot', 'aria-current': i === p.index ? 'true' : undefined, 'aria-label': 'Страница ' + (i + 1) + ' из ' + n,
        onClick: function () { p.onChange && p.onChange(i); } }, h('span', null)));
    })(i);
    return h('div', { className: cx('og-dots', p.onDark && 'og-dots--dark', p.className), role: 'group', 'aria-label': p.label || 'Страницы' }, out);
  }

  var api = { Button: Button, OrbButton: OrbButton, Segmented: Segmented, WindowBar: WindowBar, CategoryTabs: CategoryTabs, BottomNav: BottomNav, Panel: Panel, Accordion: Accordion, List: List, ListItem: ListItem, ActionTile: ActionTile, Dialog: Dialog, TextField: TextField, Select: Select, Checkbox: Checkbox, RadioGroup: RadioGroup, Switch: Switch, Slider: Slider, ProgressBar: ProgressBar, Meter: Meter, Readout: Readout, Badge: Badge, Balloon: Balloon, Card: Card, Chip: Chip, ChipGroup: ChipGroup, Fab: Fab, Menu: Menu, BottomSheet: BottomSheet, NavDrawer: NavDrawer, Tabs: Tabs, Snackbar: Snackbar, Tooltip: Tooltip, SearchBar: SearchBar, Spinner: Spinner, Divider: Divider, Avatar: Avatar, Stepper: Stepper, DatePicker: DatePicker, Banner: Banner, Skeleton: Skeleton, CodeInput: CodeInput, EmptyState: EmptyState, ListSection: ListSection, SwipeRow: SwipeRow, PullRefresh: PullRefresh, ChatBubble: ChatBubble, TypingIndicator: TypingIndicator, Composer: Composer, PageDots: PageDots, Icon: Icon };
  window.OldgeUI = Object.assign(window.OldgeUI || {}, api);
})();
