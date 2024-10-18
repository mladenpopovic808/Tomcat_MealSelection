# Meal Selection Web Application

This project is a web application for a client in the food service and delivery industry, designed to allow IT firm employees to select meals for each working day of the week. The client will use the app to track meal orders and manage meal quantities for the upcoming week.

## Features

- **Meal Selection**: Users can choose one meal from a predefined set of three for each working day of the week (Monday to Friday).
- **Single Submission**: Each user can select meals only once per session. After submitting the form, they will be redirected to a confirmation page and cannot access the meal selection page again during the same session.
- **Order Overview for Client**: The client can view a summary of all meal orders, grouped by day. There is also an option to clear all orders for a fresh start.
- **Meal Data**: Available meals are loaded from files (`ponedeljak.txt`, `utorak.txt`, etc.) upon server startup.
- **Admin Access**: Access to the selected meals overview is protected by a password, which is stored in a `password.txt` file.

## Technical Requirements

- The application is built using **Servlet API**.
- **Tomcat 9.0.73** is required to run the application.
- Meal data for each day is read from text files upon server initialization.
- The state of the application (loaded meals and selected meals) is stored at the servlet instance level.
- Users cannot resubmit their choices in the same session once the form is submitted.

## How It Works

### User Functionality

1. **Meal Selection Page**: 
   - The user is presented with a form to select meals for each working day (Monday to Friday).
   - Each day's selection includes three meal options loaded from files (e.g., `ponedeljak.txt`, `utorak.txt`).
   - All fields are mandatory.
   
2. **Order Submission**: 
   - Upon submission, the user's choices are stored and they are redirected to a confirmation page.
   - If the user tries to access the selection page again during the same session, they will see a message stating that their order has already been placed, along with their previously selected meals.

### Admin Functionality

1. **Order Overview**: 
   - The client can access an overview of all selected meals, grouped by day, via a URL that includes a password query parameter (e.g., `/odabrana-jela?lozinka={password}`).
   - The valid password is stored in the `password.txt` file and is loaded when the servlet is initialized.

2. **Clearing Orders**: 
   - The client has the option to delete all meal orders, resetting the system for the next week. After clearing orders, users can access the meal selection page again.



