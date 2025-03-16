# Firebase Group App

## Overview
The **Firebase Group App** is an Android application designed to simulate a successful order placement process. It allows users to view a confirmation screen with details such as order number, delivery date, and total order cost. Users can continue shopping or view their order details, providing a seamless shopping experience.

---

## Key Features
- **Order Confirmation Screen**: After an order is placed, users are shown a confirmation screen displaying their order number, delivery date, and total amount.
- **Dynamic Delivery Date**: The app calculates and displays the delivery date by adding one day to the current date.
- **Continue Shopping**: A button that redirects users to a shopping page where they can browse more products.
- **View Order Details**: A placeholder button that, in the future, will navigate to a detailed page showing full order information.
- **Firebase Integration**: The app is built with Firebase, though it's currently being used mainly for authentication. Future enhancements may include storing and retrieving order data from Firebase.

---

## App Structure

### Main Components:
1. **OrderConfirmationActivity**:
   - Displays order-related information: confirmation icon, order number, order date, and total order cost.
   - **Buttons**:
     - **Continue Shopping**: Redirects users to the product page (`ProductActivity`).
     - **View Order Details**: Currently shows a toast message, but can be expanded to show more detailed information in the future.

2. **ProductActivity**:
   - Allows users to browse products, add them to their cart, and proceed to checkout.

---

## Screenshots

### Order Confirmation Screen
- Confirmation message with a success icon.
- Order number, order date, and total amount details displayed.

---

## Technologies Used

- **Android Studio**: IDE used for building the app.
- **Firebase**: Used for authentication (potentially used for data storage in future versions).
- **Kotlin**: Programming language used for app development.
- **XML**: Layout design for the UI components.

---

## Installation and Setup

To run the Firebase Group App, follow these steps:

1. **Clone the repository**:

   ```bash
   git clone https://github.com/kishankumar2607/shopping_app_android.git
