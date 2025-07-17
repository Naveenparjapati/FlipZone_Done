 Flipzon – E-commerce Web Application

Flipzon is a full-stack e-commerce web application developed using Spring Boot and Thymeleaf. It supports two main modules: **Admin** and **Customer**, offering seamless shopping and management features. This project demonstrates core concepts of Spring MVC, RESTful services, session handling, payment integration, and cloud-based image storage.

## 🔧 Tech Stack
- **Backend:** Spring Boot (Spring MVC, Spring Data JPA)
- **Frontend:** Thymeleaf, HTML, CSS, JavaScript
- **Database:** MySQL
- **Image Storage:** Cloudinary API
- **Payment Gateway:** Razorpay API
- **Build Tool:** Maven

## ✨ Features

### 🛒 Customer Module
- User Registration with Email Verification
- Secure Login & Logout
- Browse Products with:
  - Search
  - Filters (e.g., by category)
  - Pagination
- Product Detail View
- Add/Remove Items from Cart
- Checkout with Razorpay Integration
- Order Summary and Success Page
- Session-based Cart Tracking

### 🔐 Admin Module
- Admin Login
- Product Management:
  - Add/Edit/Delete Products
  - Upload Images via Cloudinary API
- View All Products
- 
## 📸 Integrations

- **Cloudinary API** – Upload and store product images in the cloud.
- **Razorpay API** – Handle secure and real-time online payments.

## 🏗️ Project Structure

```
flipzon/
├── src/
│   ├── main/
│   │   ├── java/com/flipzon/
│   │   ├── resources/
│   │   │   ├── templates/        # Thymeleaf HTML files
│   │   │   ├── static/           # CSS, JS, images
│   │   │   └── application.properties
├── pom.xml
└── README.md

## ⚙️ Setup Instructions

### Prerequisites
- Java 17+
- Maven
- MySQL Database
- Cloudinary Account (API Key & Secret)
- Razorpay Account (API Key & Secret)

````

### 1. Configure Application Properties

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/flipzon
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

cloudinary.cloud_name=your_cloud_name
cloudinary.api_key=your_api_key
cloudinary.api_secret=your_api_secret

razorpay.key_id=your_key_id
razorpay.key_secret=your_key_secret

## ✅ Future Enhancements

* Add product reviews and ratings
* Order history and invoice generation
* Role-based access control
* Inventory stock tracking
---

## 🙋‍♂️ Author

**Naveen **
📧 naveenkumarp1009@gmail.com
🔗 
 linkedin.com/in/naveen-kumar-3b8b6a23b
 https://leetcode.com/u/Naveenkumar0628/
 
