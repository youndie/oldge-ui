# NavDrawer

Боковое меню — аналог Material Navigation drawer: шапка в раме скина с аватаром, пункты навигации, активный — пилюлей.

**Потребитель даёт:** `items` `{id, icon, label, badge?}` и `{section: 'Заголовок'}`, `value`, `onChange`, `title`, `subtitle`, `avatar`, `open`, `onClose`; `inline` — без затемнения и фиксированного положения.

- Выезжает слева с пружиной, пункты догоняют лесенкой.
- Для 3–5 основных разделов лучше BottomNav; drawer — когда разделов больше.
