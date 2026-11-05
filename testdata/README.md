# Sample data

Eight CSV files, one per table of the final ERD. The files are generated, not typed by hand, so
they can be reproduced exactly (`_gen/gen_testdata.py` seeds the generator with 314) and are large
enough for the brief's "at least 100 records per datatype".

| File | Rows | Columns |
|---|---|---|
| `accounts.csv` | 400 | `account_id, username, email, password, role, status, created_at` |
| `profiles.csv` | 400 | `profile_id, account_id, full_name, phone, address, status, created_at, updated_at` |
| `companies.csv` | 100 | `company_id, owner_account_id, name, description, location, contact_email, phone, logo_url, status` |
| `categories.csv` | 100 | `category_id, name, description, status` |
| `projects.csv` | 300 | `project_id, owner_account_id, category_id, title, description, style, budget, stage, completion_date, view_count, shortlist_count, created_at, updated_at` |
| `images.csv` | 400 | `image_id, project_id, image_url, display_order, is_cover, uploaded_at` |
| `favorites.csv` | 400 | `favorite_id, customer_account_id, project_id, saved_at` |
| `events.csv` | 2000 | `event_id, actor_account_id, project_id, event_type, occurred_at` |

## What the rows represent

* **accounts** - 100 accounts per role, ids 1-100 User Admin, 101-200 Interior Designer,
  201-300 Customer, 301-400 Platform Management. Usernames are `user0001` ... `user0400`, emails
  `user0001@idp.test`, and the password of row *n* is `Passw0rd!n`. One account in every fifty is
  `SUSPENDED`, which gives the suspend and login-failure tests real data to work with.
* **profiles** - exactly one per account, so the "one profile per account" rule holds in the data
  as well as in the code.
* **companies** - one per Interior Designer, owned by accounts 101-200.
* **categories** - twelve realistic names (Kitchen, Living Room, Bedroom, ...) followed by numbered
  ones, all `ACTIVE` except a few `INACTIVE`, so the "category must be active" rule can be tested.
* **projects** - 300 projects: 200 `CURRENT` (`completion_date` empty) and 100 `PAST` (with a
  completion date), owned by the designer accounts and filed under a category. `view_count` and
  `shortlist_count` are filled in, so the designer's interest screens have something to total.
* **images** - a gallery per project, `display_order` 0 being the cover image.
* **favorites** - 100 customers with four saved projects each, so no customer/project pair repeats.
* **events** - the activity log the reports count: mostly `PROJECT_VIEW`, plus `PROJECT_SHORTLIST`,
  `PROJECT_CREATE`, `PROJECT_UPDATE` and `STATUS_CHANGE`, spread over several months.

## Loading

`src/test/java/idp/testdata/TestDataLoader.java` reads the files into the in-memory database:

```java
TestDataLoader.Summary summary = new TestDataLoader().load();
System.out.println(summary);   // accounts=400, profiles=400, ...
```

The loader looks for the directory in this order: the `idp.testdata` system property, `./testdata`,
`./IDP/testdata`, `../testdata`. `TestDataLoaderTest` runs it and asserts the row counts, the
foreign keys and that a loaded account can log in.

## Regenerating

```bash
python _gen/gen_testdata.py
```

The generator is deterministic (`random.seed(314)`), so the files come back identical. Keep the
column order - the loader reads the header row, but the DDL in `database/schema.sql` and these
files are meant to be read side by side.
