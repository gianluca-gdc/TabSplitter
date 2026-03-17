# TabSplitter

Splitting a check with a group is annoying. Someone always does the math wrong, 
nobody knows how to split tax proportionally, and half the people forget to Venmo 
you. TabSplitter fixes that.

Built with Kotlin Multiplatform and Compose Multiplatform as a solo project — 
Android first, with the architecture already set up for iOS and desktop.

---

## What it does

You take a photo of a receipt or enter items manually. The app uses on-device OCR 
to pull out item names and prices automatically. You add the people at the table, 
drag items to whoever ordered them, set tip and tax, and it handles the rest — 
each person gets an exact total with a proportional share of tax and tip, not just 
a flat split.

When you're done it sends each person an SMS with what they owe and a payment link 
for Venmo, Cash App, or Zelle. The payer never gets a message sent to themselves.

---

## Stack

- Kotlin Multiplatform / Compose Multiplatform
- Room for local storage (participants, receipts, payment handles)
- Google ML Kit for receipt OCR
- ViewModel + StateFlow for state management
- Platform-specific payment deep links (Venmo, Cash App, Zelle)

---

## How the math works

Each person only pays for items assigned to them. Tax and tip are distributed 
proportionally based on each person's subtotal relative to the total — so if you 
ordered more expensive food you pay a larger share of tax and tip. Everything 
rounds to two decimals.

---

## Running it
```bash
./gradlew installDebug
```

Or just open in Android Studio and run.

---

## Status

Core flow is fully functional on Android. Multi-user real-time syncing and 
iOS support are the next steps.
