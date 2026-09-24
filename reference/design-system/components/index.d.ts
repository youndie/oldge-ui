import type * as React from 'react';

export type IconName = 'home' | 'search' | 'gear' | 'user' | 'bell' | 'star' | 'doc' | 'image' | 'note' | 'film' | 'folder' | 'disc' | 'flame' | 'cloud' | 'lock' | 'grid' | 'download' | 'upload' | 'refresh' | 'plus' | 'minus' | 'edit' | 'trash' | 'play' | 'pause' | 'levels' | 'back' | 'chevron' | 'down' | 'chevrons' | 'menu' | 'close' | 'check' | 'help' | 'info' | 'warn' | 'ok' | 'error' | 'eye' | 'eye-off' | 'attach' | 'send' | 'more' | 'heart' | 'share' | 'key' | 'mail';

/** Single-ink 24px glyph on currentColor. Decorative unless `label` is set. */
export interface IconProps { name: IconName; size?: number; label?: string; className?: string }
export declare function Icon(props: IconProps): React.ReactElement;

/* ── Actions ── */
/** Glossy push button. primary = the skin accent (one per screen), secondary = silver chrome, plain = text link. */
export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> { variant?: 'primary' | 'secondary' | 'plain'; size?: 'sm' | 'md' | 'lg'; icon?: IconName; block?: boolean }
export declare function Button(props: ButtonProps): React.ReactElement;

/** Round glossy button in a chrome rim. `label` is required (it is the accessible name). */
export interface OrbButtonProps extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, 'children'> { icon: IconName; label: string; size?: 'sm' | 'md' | 'lg'; tone?: 'bezel' | 'accent' | 'chrome' }
export declare function OrbButton(props: OrbButtonProps): React.ReactElement;

export interface SegmentOption { id: string; label: string; icon?: IconName }
/** Pill-shaped single choice among 2–4 options. */
export interface SegmentedProps { options: SegmentOption[]; value: string; onChange?: (id: string) => void; label?: string; className?: string }
export declare function Segmented(props: SegmentedProps): React.ReactElement;

/* ── Navigation ── */
export interface BarAction { icon: IconName; label: string; onPress?: () => void; tone?: 'bezel' | 'accent' | 'chrome' }
/** Top of every screen: the skin's bezel with title and small orb actions. */
export interface WindowBarProps { title: React.ReactNode; subtitle?: React.ReactNode; icon?: IconName; lead?: React.ReactNode; onBack?: () => void; backLabel?: string; center?: React.ReactNode; actions?: BarAction[]; square?: boolean; overlay?: boolean; className?: string }
export declare function WindowBar(props: WindowBarProps): React.ReactElement;

export interface NavItem { id: string; icon: IconName; label: string }
/** Row of large glyphs; only the active one shows its name, underlined with a hook. 4–8 items, scrolls. */
export interface CategoryTabsProps { items: NavItem[]; value: string; onChange?: (id: string) => void; label?: string; className?: string }
export declare function CategoryTabs(props: CategoryTabsProps): React.ReactElement;

/** Bottom bezel dock, 3–5 items; the active icon sits in a chrome capsule. */
export interface BottomNavProps { items: NavItem[]; value: string; onChange?: (id: string) => void; label?: string; square?: boolean; className?: string }
export declare function BottomNav(props: BottomNavProps): React.ReactElement;

/* ── Containers ── */
/** Inset surface with an optional small title; `sunken` for read-only output. */
export interface PanelProps { title?: React.ReactNode; sunken?: boolean; flush?: boolean; children?: React.ReactNode; className?: string }
export declare function Panel(props: PanelProps): React.ReactElement;

/** Collapsible section: pill header with an orb badge, inset body. */
export interface AccordionProps { title: React.ReactNode; icon?: IconName; open?: boolean; defaultOpen?: boolean; onToggle?: (open: boolean) => void; children?: React.ReactNode; className?: string }
export declare function Accordion(props: AccordionProps): React.ReactElement;

/** Rounded inset list; each child becomes a row. */
export interface ListProps { children?: React.ReactNode; striped?: boolean; bare?: boolean; label?: string; className?: string }
export declare function List(props: ListProps): React.ReactElement;

/** One row: icon, title, subtitle, value / trailing control, chevron. Becomes a button when `onPress` is set. */
export interface ListItemProps { title: React.ReactNode; subtitle?: React.ReactNode; icon?: IconName | React.ReactNode; value?: React.ReactNode; trailing?: React.ReactNode; chevron?: boolean; selected?: boolean; dense?: boolean; onPress?: () => void; className?: string }
export declare function ListItem(props: ListItemProps): React.ReactElement;

/** Launcher action: big glossy orb badge plus bold label, no card. */
export interface ActionTileProps { title: React.ReactNode; description?: React.ReactNode; icon?: IconName; tone?: 'chrome' | 'accent' | 'bezel'; layout?: 'row' | 'stack'; onPress?: () => void; className?: string }
export declare function ActionTile(props: ActionTileProps): React.ReactElement;

/** Window-style dialog: bezel frame, title, close orb, body on the skin's body. `modal` adds the scrim. */
export interface DialogProps { title: React.ReactNode; icon?: IconName; children?: React.ReactNode; actions?: React.ReactNode; onClose?: () => void; open?: boolean; modal?: boolean; className?: string }
export declare function Dialog(props: DialogProps): React.ReactElement | null;

/* ── Forms ── */
/** Sunken text field. `reveal` = password with a show/hide orb; `multiline` = textarea. Grows with the system font size. */
export interface TextFieldProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange'> { label?: React.ReactNode; help?: React.ReactNode; error?: string | boolean; icon?: IconName; reveal?: boolean; multiline?: boolean; rows?: number; onChange?: (value: string, event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => void }
export declare function TextField(props: TextFieldProps): React.ReactElement;

export interface SelectOption { value: string; label: string; icon?: IconName }
/** Chrome-framed picker with an accent drop button; native select underneath. */
export interface SelectProps { options: SelectOption[]; value: string; onChange?: (value: string) => void; label?: React.ReactNode; ariaLabel?: string; disabled?: boolean; className?: string }
export declare function Select(props: SelectProps): React.ReactElement;

export interface CheckboxProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'type'> { label: React.ReactNode; onChange?: (checked: boolean, event: React.ChangeEvent<HTMLInputElement>) => void }
export declare function Checkbox(props: CheckboxProps): React.ReactElement;

export interface RadioOption { value: string; label: React.ReactNode; disabled?: boolean }
export interface RadioGroupProps { options: RadioOption[]; value?: string; defaultValue?: string; onChange?: (value: string) => void; label?: React.ReactNode; name?: string; className?: string }
export declare function RadioGroup(props: RadioGroupProps): React.ReactElement;

/** On/off with an ON/OFF lamp in the track. */
export interface SwitchProps extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'onChange' | 'type'> { label: React.ReactNode; hint?: React.ReactNode; onChange?: (checked: boolean, event: React.ChangeEvent<HTMLInputElement>) => void }
export declare function Switch(props: SwitchProps): React.ReactElement;

export interface SliderProps { value: number; onChange?: (value: number) => void; label?: string; valueText?: string; disabled?: boolean; className?: string }
export declare function Slider(props: SliderProps): React.ReactElement;

/* ── Feedback ── */
/** XP chunked progress. No `value` = indeterminate. */
export interface ProgressBarProps { value?: number; label?: React.ReactNode; detail?: React.ReactNode; status?: 'done' | 'error'; className?: string }
export declare function ProgressBar(props: ProgressBarProps): React.ReactElement;

/** Segmented LCD level: storage, signal, load. */
export interface MeterProps { value: number; label: string; valueText?: string; segments?: number; className?: string }
export declare function Meter(props: MeterProps): React.ReactElement;

/** LCD number readout with unlit 88 ghost digits. */
export interface ReadoutProps { value: string | number; label?: string; unit?: string; size?: 'md' | 'sm'; className?: string }
export declare function Readout(props: ReadoutProps): React.ReactElement;

export interface BadgeProps { tone?: 'neutral' | 'accent' | 'success' | 'warning' | 'danger'; count?: boolean; children?: React.ReactNode; className?: string }
export declare function Badge(props: BadgeProps): React.ReactElement;

/** Yellow tray balloon for non-blocking notices. */
export interface BalloonProps { title?: React.ReactNode; children?: React.ReactNode; icon?: 'info' | 'ok' | 'warn' | 'error'; tail?: 'bottom' | 'top' | 'none'; onClose?: () => void; className?: string }
export declare function Balloon(props: BalloonProps): React.ReactElement;

/* ── Material-style additions ── */
/** Material Card: optional bezel bar, media, text, actions; `onPress` makes the whole card a button. */
export interface CardProps { title?: React.ReactNode; subtitle?: React.ReactNode; children?: React.ReactNode; media?: IconName | React.ReactNode; image?: string; imageAlt?: string; bar?: React.ReactNode; barIcon?: IconName; actions?: React.ReactNode; variant?: 'raised' | 'sunken'; onPress?: () => void; className?: string }
export declare function Card(props: CardProps): React.ReactElement;

/** Material Chips: assist (action), filter (toggle), input (removable value). */
export interface ChipProps { children: React.ReactNode; kind?: 'assist' | 'filter' | 'input'; icon?: IconName; selected?: boolean; onPress?: () => void; onRemove?: () => void; disabled?: boolean; className?: string }
export declare function Chip(props: ChipProps): React.ReactElement;
export interface ChipGroupProps { children?: React.ReactNode; label?: string; scroll?: boolean; className?: string }
export declare function ChipGroup(props: ChipGroupProps): React.ReactElement;

/** Material FAB in a chrome frame. `label` makes it extended; `expanded` spins the icon 135°. */
export interface FabProps extends Omit<React.ButtonHTMLAttributes<HTMLButtonElement>, 'children'> { icon?: IconName; label?: string; ariaLabel?: string; tone?: 'accent' | 'chrome'; expanded?: boolean; docked?: boolean }
export declare function Fab(props: FabProps): React.ReactElement;

export interface MenuItem { id?: string; label?: string; icon?: IconName; hint?: string; danger?: boolean; divider?: boolean }
/** XP-style dropdown menu anchored to `trigger`. Closes on select, outside tap, Escape. */
export interface MenuProps { trigger: React.ReactElement; items: MenuItem[]; onSelect?: (id: string) => void; align?: 'start' | 'end'; label?: string; defaultOpen?: boolean }
export declare function Menu(props: MenuProps): React.ReactElement;

/** Material bottom sheet in the skin's bezel. */
export interface BottomSheetProps { title: React.ReactNode; children?: React.ReactNode; open?: boolean; onClose?: () => void; inline?: boolean; className?: string }
export declare function BottomSheet(props: BottomSheetProps): React.ReactElement | null;

export interface DrawerItem { id?: string; icon?: IconName; label?: string; badge?: string; section?: string }
/** Material navigation drawer: bezel header, pill-marked active item. */
export interface NavDrawerProps { items: DrawerItem[]; value?: string; onChange?: (id: string) => void; title?: React.ReactNode; subtitle?: React.ReactNode; avatar?: React.ReactNode; open?: boolean; onClose?: () => void; inline?: boolean; label?: string; className?: string }
export declare function NavDrawer(props: NavDrawerProps): React.ReactElement | null;

export interface TabItem { id: string; label: string; icon?: IconName; badge?: string }
/** XP property-sheet tabs; `children` is the active panel. */
export interface TabsProps { items: TabItem[]; value: string; onChange?: (id: string) => void; label?: string; children?: React.ReactNode; className?: string }
export declare function Tabs(props: TabsProps): React.ReactElement;

/** Material snackbar in the bezel, one action. */
export interface SnackbarProps { children: React.ReactNode; actionLabel?: string; onAction?: () => void; onClose?: () => void; duration?: number; open?: boolean; inline?: boolean; className?: string }
export declare function Snackbar(props: SnackbarProps): React.ReactElement | null;

/** Small yellow XP tooltip; hover, focus or long-press. */
export interface TooltipProps { label: string; children: React.ReactElement; placement?: 'above' | 'below'; defaultOpen?: boolean }
export declare function Tooltip(props: TooltipProps): React.ReactElement;

export interface SearchBarProps { value?: string; defaultValue?: string; onChange?: (value: string) => void; onSubmit?: (value: string) => void; placeholder?: string; label?: string; trailing?: React.ReactNode; className?: string }
export declare function SearchBar(props: SearchBarProps): React.ReactElement;

/** Indeterminate progress: a spinning glossy disc. */
export interface SpinnerProps { size?: 'sm' | 'md' | 'lg'; label?: string; className?: string }
export declare function Spinner(props: SpinnerProps): React.ReactElement;

export interface DividerProps { label?: string; className?: string }
export declare function Divider(props: DividerProps): React.ReactElement;

export interface AvatarProps { name?: string; src?: string; icon?: IconName; size?: 'sm' | 'md' | 'lg'; status?: 'online' | 'busy' | 'away'; className?: string }
export declare function Avatar(props: AvatarProps): React.ReactElement;

/** Wizard steps: done, current (pulsing), upcoming. */
export interface StepperProps { steps: string[]; current?: number; label?: string; className?: string }
export declare function Stepper(props: StepperProps): React.ReactElement;

/** Month calendar; value is an ISO date (YYYY-MM-DD); weeks start on Monday. */
export interface DatePickerProps { value?: string; onChange?: (iso: string) => void; label?: string; className?: string }
export declare function DatePicker(props: DatePickerProps): React.ReactElement;

/** XP information bar (Material Banner). */
export interface BannerProps { title?: React.ReactNode; children?: React.ReactNode; icon?: IconName; actions?: React.ReactNode; boxed?: boolean; open?: boolean; className?: string }
export declare function Banner(props: BannerProps): React.ReactElement | null;

/** Loading placeholder: sunken scanline slots with an accent chunk scanner; circles are segmented dials. `row` and `card` are ready-made compositions. */
export interface SkeletonProps { variant?: 'text' | 'circle' | 'rect' | 'row' | 'card'; lines?: number; width?: number | string; height?: number | string; icon?: IconName; index?: number; className?: string }
export declare function Skeleton(props: SkeletonProps): React.ReactElement;

/* ── Found by the stress screens ── */
/** One-time code on LCD cells over a single real input (paste and SMS autofill work). */
export interface CodeInputProps { length?: number; value?: string; defaultValue?: string; onChange?: (code: string) => void; onComplete?: (code: string) => void; label?: React.ReactNode; help?: React.ReactNode; error?: string | boolean; className?: string }
export declare function CodeInput(props: CodeInputProps): React.ReactElement;

/** Empty, error or offline state in place of content. */
export interface EmptyStateProps { title: React.ReactNode; children?: React.ReactNode; icon?: IconName; tone?: 'empty' | 'error' | 'offline'; action?: React.ReactNode; className?: string }
export declare function EmptyState(props: EmptyStateProps): React.ReactElement;

/** List section with a sticky pill header. */
export interface ListSectionProps { title: React.ReactNode; meta?: React.ReactNode; children?: React.ReactNode; className?: string }
export declare function ListSection(props: ListSectionProps): React.ReactElement;

export interface SwipeAction { icon: IconName; label: string; tone?: 'chrome' | 'accent' | 'danger'; onPress?: () => void }
/** Row whose actions are revealed by swiping left. */
export interface SwipeRowProps { children: React.ReactNode; actions: SwipeAction[]; defaultOpen?: boolean; className?: string }
export declare function SwipeRow(props: SwipeRowProps): React.ReactElement;

/** Pull-to-refresh with a spinning disc; wraps the scroll content. */
export interface PullRefreshProps { children?: React.ReactNode; onRefresh?: () => void; refreshing?: boolean; height?: number | string; label?: string; className?: string }
export declare function PullRefresh(props: PullRefreshProps): React.ReactElement;

/** Chat message bubble; group consecutive messages with `tail={false}` on all but the last. */
export interface ChatBubbleProps { from: 'me' | 'them'; children: React.ReactNode; time?: string; status?: 'sent' | 'read'; avatar?: React.ReactNode; author?: React.ReactNode; tail?: boolean; className?: string }
export declare function ChatBubble(props: ChatBubbleProps): React.ReactElement;

export interface TypingIndicatorProps { name?: string; avatar?: React.ReactNode; className?: string }
export declare function TypingIndicator(props: TypingIndicatorProps): React.ReactElement;

/** Message input in the bezel: attach, auto-growing textarea, send (Enter sends, Shift+Enter breaks the line). */
export interface ComposerProps { onSend?: (text: string) => void; value?: string; onChange?: (text: string) => void; placeholder?: string; label?: string; onAttach?: (() => void) | false; className?: string }
export declare function Composer(props: ComposerProps): React.ReactElement;

/** Page indicator for carousels; each dot is a 44px button. */
export interface PageDotsProps { count: number; index: number; onChange?: (index: number) => void; onDark?: boolean; label?: string; className?: string }
export declare function PageDots(props: PageDotsProps): React.ReactElement;

declare global {
  interface Window {
    OldgeUI: { Button: typeof Button; OrbButton: typeof OrbButton; Segmented: typeof Segmented; WindowBar: typeof WindowBar; CategoryTabs: typeof CategoryTabs; BottomNav: typeof BottomNav; Panel: typeof Panel; Accordion: typeof Accordion; List: typeof List; ListItem: typeof ListItem; ActionTile: typeof ActionTile; Dialog: typeof Dialog; TextField: typeof TextField; Select: typeof Select; Checkbox: typeof Checkbox; RadioGroup: typeof RadioGroup; Switch: typeof Switch; Slider: typeof Slider; ProgressBar: typeof ProgressBar; Meter: typeof Meter; Readout: typeof Readout; Badge: typeof Badge; Balloon: typeof Balloon; Card: typeof Card; Chip: typeof Chip; ChipGroup: typeof ChipGroup; Fab: typeof Fab; Menu: typeof Menu; BottomSheet: typeof BottomSheet; NavDrawer: typeof NavDrawer; Tabs: typeof Tabs; Snackbar: typeof Snackbar; Tooltip: typeof Tooltip; SearchBar: typeof SearchBar; Spinner: typeof Spinner; Divider: typeof Divider; Avatar: typeof Avatar; Stepper: typeof Stepper; DatePicker: typeof DatePicker; Banner: typeof Banner; Skeleton: typeof Skeleton; CodeInput: typeof CodeInput; EmptyState: typeof EmptyState; ListSection: typeof ListSection; SwipeRow: typeof SwipeRow; PullRefresh: typeof PullRefresh; ChatBubble: typeof ChatBubble; TypingIndicator: typeof TypingIndicator; Composer: typeof Composer; PageDots: typeof PageDots; Icon: typeof Icon };
  }
}
