# Atlen

## Overview
**Atlen** is a comprehensive travel planning application for Android that allows users to seamlessly plan their trips—from discovering destinations and exploring flights, hotels, and restaurants to creating daily itineraries and managing travel expenses. With a modern interface built using Jetpack Compose, Atlen provides a smooth and intuitive user experience.

## Features

### Authentication
- **Email Login**: Users can log in using their email credentials.
- **OTP Verification**: Secure one-time password (OTP) validation for login or password recovery.
- **Google OAuth 2.0**: Option to log in using a Google account for quicker access.
- **Session Management**: Token-based authentication system for maintaining secure sessions.

## Core Features

### Destination Discovery
- **Search Destinations**: Users can search for their travel destination by name.
- **View Nearby Options**: Once a destination is selected, users can view:
  - **Flights** to the location
  - **Hotels** near the area
  - **Restaurants** around the destination

### Trip Planning
- **Itinerary Builder**: Create and customize a detailed trip plan with activities for each day.
- **Daily Scheduler**: Assign specific activities to particular days of your trip.
- **Expense Tracker**: Add and manage all trip-related expenses to keep track of the budget.

## Architecture
- **MVVM Architecture**: Ensures clear separation of concerns between the UI and business logic.
- **Jetpack Compose**: Modern declarative UI toolkit for Android used to create responsive and dynamic UIs.
- **Dagger Hilt**: Used for efficient dependency injection across ViewModels and repositories.
- **Coroutines**: Asynchronous programming for network and database operations.

## Screenshots

### Authentication

  <img src="https://github.com/user-attachments/assets/f1e4fa08-1218-4ede-90bb-ccbd3c2dd23f" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/4b8997fc-3633-41a2-9207-3f5807e7f1e5" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/277e92d9-541c-465c-ae73-0d2879c384c4" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/39810cb0-b24e-45b3-973c-7cb69cea1072" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/70dec2f0-5c64-45cc-b201-e190bac657fc" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/f64f58ba-cc4a-45d6-aed3-476c98ce06ca" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/7dbdfd4b-a97b-4291-8fa1-611b715abce9" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/160a56f0-a77a-4af1-889b-b0ae04840dc3" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/3e6c71ad-f14a-4e8e-aeed-fecf4c5a1dd6" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/f8476fbf-2a4d-43d9-b60d-ba35b1238eb2" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/b254bbf8-2612-4477-9545-ed8bbee4d2c1" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/21ec4ccf-d9c7-404e-8af6-b44b505816c0" width="300" height="600" />


### Destination Search, Discovery, Trip Itinerary & Expenses

  <img src="https://github.com/user-attachments/assets/59b36ddc-6e41-4ed4-b59d-a1ce722c4dfa" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/2cf76510-3305-4a02-8475-914583336515" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/dd0542bc-d62f-4442-86e0-84cfe9ba7017" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/154f1e0b-7d95-49f3-9bbc-7d0313b84ae7" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/cadd35b3-84f1-41ba-94aa-f3749010e809" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/78ef5602-9378-4317-965e-9cd963bccd4a" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/6102ad38-b096-4902-ab0e-14935f854779" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/4c6edf6f-7420-4555-9b2d-106a9bf64550" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/e00d7c83-3f6e-4fbb-98ea-06afafc12769" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/33c9118e-5792-4662-b0c6-1db8efe5848d" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/07db33df-bf6d-4480-a0f1-1fdfba417d8c" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/e648297b-82ef-4a94-9d57-f2172ffd4289" width="300" height="600" />
  <img src="https://github.com/user-attachments/assets/49546370-fffc-429a-8c8e-93fe3e9bddd1" width="300" height="900" />
  <img src="https://github.com/user-attachments/assets/6fc2e98a-bd0e-43c0-8f70-f6d205cee3d6" width="300" height="900" />


## Dependencies

Atlen leverages the following major dependencies:
- **Jetpack Compose**: For building modern UIs.
- **Retrofit**: For making API calls.
- **Kotlin Coroutines**: For handling background operations efficiently.
- **Dagger Hilt**: For dependency injection.
- **Navigation Compose**: For navigating between screens.
- **Room**: For local data persistence.
- **Google OAuth API**: For Google sign-in functionality.

## Contributing

Contributions are welcome! Here's how to contribute:
1. Fork the repository.
2. Create a new branch: `git checkout -b feature-name`.
3. Make your changes and commit: `git commit -m 'Add feature'`.
4. Push to your fork: `git push origin feature-name`.
5. Create a pull request explaining your changes.

## Contact

For questions, suggestions, or feedback, please contact the maintainer.
