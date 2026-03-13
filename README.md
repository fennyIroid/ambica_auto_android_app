# Ambica Auto App

Android application for automotive workshop management, built with Kotlin, Jetpack Compose, and Hilt.

## Tech Stack

- **Language:** Kotlin 1.9+
- **UI:** Jetpack Compose, Material 3
- **Architecture:** MVVM with Use Cases
- **DI:** Hilt
- **Networking:** Retrofit, Gson
- **Navigation:** Compose Navigation
- **State:** StateFlow, collectAsStateWithLifecycle

## Project Structure

```
app/src/main/java/com/ambica/auto/app/
├── data/                    # Data layer
│   ├── repository/          # Repositories (JobService, etc.)
│   └── source/
│       ├── local/           # Session, DataStore, Demo data
│       └── remote/          # API, models, EndPoints
├── di/                      # Hilt modules
├── model/domain/            # Domain models (Job, StaffRole, etc.)
├── navigation/              # Nav routes, actions, graphs
├── ui/compose/common/       # Reusable UI components
└── ux/
    ├── startup/             # Splash, Login, Forgot/Set/Change Password
    ├── main/                # Bottom nav screens (Dashboard, Jobs, Profile)
    ├── home/                # Home screen (dashboard content)
    └── container/           # Non-bottom-nav screens
        ├── branches/        # Branch management
        │   ├── create/      # Create branch
        │   ├── edit/        # Edit branch
        │   └── detail/      # Branch detail
        └── job/
            ├── create/      # Gate entry (create job)
            ├── hub/         # Job details hub
            └── modules/     # Job sub-modules
                ├── estimate_docs/
                ├── insurance_survey/
                ├── repair_progress/
                ├── spare_parts/
                ├── billing_payment/
                ├── gate_pass/
                └── timeline/
```

## Features

### Authentication
- Splash screen with session check
- Login (email, password, role)
- Forgot password
- Set password (first-time)
- Change password
- Logout

### Dashboard & Home
- Dashboard with job metrics
- Quick actions: Jobs, Branches
- Job list with status filters

### Branch Management
- **List:** Search, filter by status (All/Active/Inactive), delete
- **Create:** Full form (code, name, location, tagline, address, phone, email, status)
- **Edit:** Update branch via API (`/branches/update/`)
- **Detail:** View branch info, edit button (for users with `canManageBranches`)
- **Redirect:** After create/edit success, immediate redirect to Branches list with refresh and success toast

### Job Management
- Jobs list with status chips
- Job details hub with role-based section visibility
- **Sections:** Overview, Estimate (Documents), Insurance, Repair Progress, Parts Tracking, Billing, Gate Pass, Notes
- **Role visibility:** Owner, Manager, System Admin, Admin see all sections; other roles see subsets

### Job Modules
- **Gate Entry:** Create new job
- **Estimate Docs:** Document management
- **Insurance Survey:** Multi-step form with document checklist from API
- **Repair Progress:** Stage tracking
- **Spare Parts:** Parts tracking
- **Billing & Payment:** Payment details
- **Gate Pass:** Gate pass generation
- **Timeline:** Job timeline

### Insurance Survey
- 4-step flow: Details → Checklist → Reports → Submit
- **Document checklists:** Fetched from `/document-checklists/` API (filter: `applicable_for=1` for Insurance)
- Groups by `document_category` (Vehicle, Customer, Insurance, Legal)
- Step header with horizontal scroll for consistent layout
- Approval status: Pending, Approved, Re-inspection

### APIs Integrated
- Auth: login, profile, logout, change/forgot/reset/set password, verify OTP
- Branches: list, create, detail, update, delete
- Document checklists: paginated list with search, filters, ordering

## Configuration

- **Base URL:** Set in `app/build.gradle.kts` via `BuildConfig.BASE_URL` (default: `http://157.245.106.111:8003/`)
- **Min SDK:** 26 | **Target SDK:** 35

## Build

```bash
./gradlew assembleDebug
```

## .gitignore

Project uses a standard Android `.gitignore` covering Gradle, build outputs, IDE files, keystores, and local configuration.
