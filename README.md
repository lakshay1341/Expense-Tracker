# Expense Tracker

A menu-driven Java console app for tracking personal expenses — built as a solution to the [roadmap.sh Expense Tracker project](https://roadmap.sh/projects/expense-tracker).

## Features

- Add new expenses with date, description, amount, and category.
- Update and delete existing expenses.
- View all expenses.
- View a summary of all expenses, or for a specific month.
- Filter expenses by category and see the category total.
- Expenses persist to a file (`expenses.txt`) between runs; descriptions with
  commas are quoted so the save file round-trips safely.

Planned extensions from the roadmap.sh spec are tracked as [good first issues](https://github.com/chaudhary-lakshay/Expense-Tracker/issues) — contributions welcome.

## Project structure

```
src/Expense_Tracker/
├── Expense.java          # expense model: date, description, amount, category
├── ExpenseStorage.java   # saves/loads expenses to expenses.txt
├── CsvUtil.java          # shared CSV escaping/parsing (safe for commas & quotes)
├── CsvUtilTest.java      # runnable self-check for CSV round-tripping
└── ExpenseTracker.java   # main class, menu loop, all commands
```

## Run it

```bash
git clone https://github.com/chaudhary-lakshay/Expense-Tracker.git
cd Expense-Tracker/src
javac Expense_Tracker/*.java
java Expense_Tracker.ExpenseTracker
```

Requires JDK 8+.

## More of my work

- [Password-Manager](https://github.com/chaudhary-lakshay/Password-Manager) — annotated teaching example of the Java AES API (and how NOT to do crypto)
- [CineTicket](https://github.com/chaudhary-lakshay/CineTicket) — Spring Boot movie ticketing platform: JWT auth, Stripe payments, PDF tickets, email
- [VitaLink](https://github.com/chaudhary-lakshay/vitalink) — remote patient monitoring backend: HL7 ADT, MQTT ingest, live ECG streaming

## License

MIT
