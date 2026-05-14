---
name: Earthy Professional
colors:
  surface: '#f8f9fa'
  surface-dim: '#d9dadb'
  surface-bright: '#f8f9fa'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#f3f4f5'
  surface-container: '#edeeef'
  surface-container-high: '#e7e8e9'
  surface-container-highest: '#e1e3e4'
  on-surface: '#191c1d'
  on-surface-variant: '#414844'
  inverse-surface: '#2e3132'
  inverse-on-surface: '#f0f1f2'
  outline: '#717973'
  outline-variant: '#c1c8c2'
  surface-tint: '#3f6653'
  primary: '#012d1d'
  on-primary: '#ffffff'
  primary-container: '#1b4332'
  on-primary-container: '#86af99'
  inverse-primary: '#a5d0b9'
  secondary: '#805533'
  on-secondary: '#ffffff'
  secondary-container: '#fdc39a'
  on-secondary-container: '#794e2e'
  tertiary: '#002d1b'
  on-tertiary: '#ffffff'
  tertiary-container: '#00452d'
  on-tertiary-container: '#74b392'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c1ecd4'
  primary-fixed-dim: '#a5d0b9'
  on-primary-fixed: '#002114'
  on-primary-fixed-variant: '#274e3d'
  secondary-fixed: '#ffdcc5'
  secondary-fixed-dim: '#f4bb92'
  on-secondary-fixed: '#301400'
  on-secondary-fixed-variant: '#653d1e'
  tertiary-fixed: '#b0f1cc'
  tertiary-fixed-dim: '#94d4b1'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#0c5136'
  background: '#f8f9fa'
  on-background: '#191c1d'
  surface-variant: '#e1e3e4'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -1px
  headline-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
  headline-lg-mobile:
    fontFamily: Plus Jakarta Sans
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
  title-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Atkinson Hyperlegible Next
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
  body-md:
    fontFamily: Atkinson Hyperlegible Next
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.5px
rounded:
  sm: 0.5rem
  DEFAULT: 1rem
  md: 1.5rem
  lg: 2rem
  xl: 3rem
  full: 9999px
spacing:
  base: 8px
  margin-mobile: 20px
  margin-desktop: 40px
  gutter: 16px
  stack-sm: 12px
  stack-md: 24px
  stack-lg: 40px
---

## Brand & Style
The design system is engineered for the intersection of high-technology startup culture and rural agricultural resilience. The brand personality is "Earthy Professional"—a synthesis of Material 3's systematic precision and the organic warmth of the pastoral landscape.

The target audience consists of modern farmers and livestock healthcare providers who require tools that feel as sturdy as their equipment and as sophisticated as their modern breeding and health tracking techniques. The UI must evoke a sense of immediate trust, absolute reliability, and deep-rooted familiarity. We utilize a refined **Modern-Corporate** aesthetic injected with **Tactile** warmth through soft shadows and organic color transitions to ensure the interface feels approachable rather than clinical.

## Colors
The palette is derived from the natural lifecycle of a farm. The **Primary (Forest Green)** represents growth and health, providing high-contrast readability for primary actions. The **Secondary (Soil Brown)** is used for structural grounding and organizational elements, lending a sense of stability. The **Tertiary (Meadow Green)** acts as a soft accent for highlight states and secondary calls to action.

The neutral palette avoids harsh blacks, utilizing deep charcoal-greens for text to reduce eye strain under direct sunlight. All interactive elements must maintain a minimum contrast ratio of 4.5:1 against the neutral background to ensure outdoor usability.

## Typography
The typography strategy prioritizes legibility and accessibility above all. **Plus Jakarta Sans** is used for headlines to provide a friendly, modern startup feel. For body copy, we use **Atkinson Hyperlegible Next**, specifically chosen for its high distinction between similar character shapes, which is critical for farmers reading medication dosages or livestock IDs in variable outdoor lighting. 

Scale is intentionally generous; body text starts at 18px on desktop and 16px on mobile to ensure ease of use for a wide demographic of users.

## Layout & Spacing
This design system employs a **Fluid Grid** model with high-density margins to prevent content from hitting the edges of the device, which is essential for handling mobile devices with rugged cases. 

- **Mobile:** 4-column grid with 20px margins.
- **Tablet:** 8-column grid with 32px margins.
- **Desktop:** 12-column grid with a max-width of 1440px and 40px margins.

We use an 8px base unit for all padding and margins to create a consistent vertical rhythm. Large vertical spacing (stack-lg) is used to clearly separate different livestock categories or health records.

## Elevation & Depth
Depth is communicated through **Tonal Layers** and soft, diffused ambient shadows. Instead of traditional gray shadows, we use a very low-opacity Primary Color tint (#1B4332 at 8-12%) for shadows to make them feel integrated into the "Earthy" environment.

- **Level 0 (Surface):** The default background, typically the neutral color.
- **Level 1 (Cards):** 2px elevation with a soft Meadow Green glow to indicate interactable containers.
- **Level 2 (Modals/Active):** 8px elevation with a more pronounced shadow for critical health alerts or input forms.
Backdrop blurs are used sparingly, only on navigation bars to maintain legibility over complex background content.

## Shapes
To reinforce the friendly and approachable brand personality, the design system utilizes **Pill-shaped** geometry. Large 24px+ corners are the standard for cards and containers, creating a "soft-tech" feel that removes any clinical coldness. Buttons always use full-rounded pill shapes. Small elements like checkboxes use a "Soft" 4px radius to ensure they remain clearly recognizable as UI controls while fitting the broader aesthetic.

## Components
- **Buttons:** Primary buttons are Forest Green with white text; secondary buttons use a Meadow Green stroke with Forest Green text. Large touch targets (min 48px height) are mandatory.
- **Livestock Chips:** Circular or pill-shaped indicators featuring illustrative icons for cattle, goats, and sheep. Use Soil Brown for ID tags and Meadow Green for "Healthy" status indicators.
- **Input Fields:** Thick 2px borders in Soil Brown when focused, with labels always visible above the field to prevent loss of context.
- **Health Cards:** Use a Level 1 elevation with a vertical color-coded bar on the left (e.g., Red for urgent vaccination, Green for updated records). 
- **Navigation:** A bottom navigation bar on mobile for easy one-handed thumb use while in the field.
- **Progressive Disclosure:** Use large, easy-to-tap accordions for detailed livestock history to keep the main interface uncluttered.