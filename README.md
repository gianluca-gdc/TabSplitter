# TabSplitter — Smart Receipt & Bill Splitting App (Kotlin Multiplatform)

**TabSplitter** is a modern, fast, and intuitive receipt-splitting app built with **Kotlin Multiplatform** and **Compose Multiplatform**.  
It lets one payer take a photo of a receipt (or enter items manually), assign each item to specific people, and instantly generate a clean per-person breakdown — including **tax**, **tip**, **shared items**, and **payment links**.

This is the fully implemented solo-MVP version, architected for future real-time multi-user syncing.

---

## Features

### Complete Bill-Splitting Flow
- Enter or scan receipt (OCR)
- Subtotal, tax %, tip %
- Add participants manually
- Link participants to phone contacts
- Auto-add payer’s name from Payment Settings
- Add items and assign each to one or multiple people
- Supports shared and individual items

### Smart Per-Person Calculation
- Only pays for items assigned to them
- Proportional tax & tip distribution
- Clean two-decimal rounding
- Transparent math and breakdown screen

### Payment & Sharing
- One-tap SMS sending via contacts
- Venmo / Cash App / Zelle link support
- Payer excluded from SMS flow
- Copy or share final summary

### Local Storage (Room)
- Saves recent participants
- Remembers linked contacts
- Stores past receipts
- Saves payment handles and payer info

### UI/UX
- Compose Multiplatform UI
- Smooth 4-step flow
- Success animation after sending messages
- Auto-returns to home

---

## Receipt Capture + OCR
The camera receipt flow includes:
- On-device text recognition  
- Auto-extraction of subtotal, tax, item names & prices  
- Ability to correct or adjust fields
- Falls back to manual entry when needed

---

## Architecture

### Frontend
- Kotlin Multiplatform  
- Compose Multiplatform  
- ViewModel + StateFlow  
- Multi-screen navigation  
- Modular state management

### Data Layer
- Room database  
  - `ReceiptEntity`  
  - `PersonEntity`  
  - `ItemEntity`  
  - `PaymentHandleEntity`

### Logic
- Modular calculation engine  
- OCR parsing layer  
- Platform-specific payment handlers  

---

## Platforms
- **Android** (fully implemented)  
- Desktop/iOS support planned

---

## Calculation Model
PersonSubtotal = sum(items assigned to person)
TaxShare = (PersonSubtotal / TotalSubtotal) * TaxAmount
TipShare = (PersonSubtotal / TotalSubtotal) * TipAmount
FinalTotal = PersonSubtotal + TaxShare + TipShare
- Shared items are evenly divided among assigned people.
- Everything is rounded to 2 decimals.

---

## Local Storage

The app saves:
- Participants  
- Linked contacts  
- Payment handles  
- Past receipts  
- OCR corrections  

Perfect for re-using common dining groups.

---

## Sharing & Exporting
- SMS sending for each person  
- Summary copied to clipboard  
- Share via any installed app  
- Never sends message to payer  

---

## Installation (Android)
./gradlew installDebug
Or run directly through Android Studio.

---

## Author
**Gianluca D. Cutugno**  
📧 thegianlucacutugno@gmail.com  
📱 845-750-5132  
💻 GitHub: https://github.com/gianluca-gdc

---

## License
MIT License  
