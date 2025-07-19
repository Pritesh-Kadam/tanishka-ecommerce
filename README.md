# tanishka-ecommerce
A modern, user-friendly jewelry e-commerce app built using React, Spring Boot, Node.js, MongoDB, PostgreSQL, and microservices architecture.
# Tanishka - Jewelry E-commerce Application

Tanishka is a modern, scalable jewelry e-commerce platform built using a microservices architecture. The application provides an elegant shopping experience with secure authentication, real-time inventory management, and admin controls — tailored for both customers and sellers.

## ✨ Features

- 🛒 Jewelry product catalog with filtering and search
- 🔐 User registration & login with JWT authentication
- 🧾 Cart, wishlist, and order management
- 📦 Inventory and product management for admins
- 📊 Admin dashboard with analytics
- 📁 Image and document upload (stored on Azure Blob)
- 📱 Responsive frontend for web and mobile devices
- 🧩 Microservices-based backend with independent services for:
  - Authentication
  - Product Catalog
  - Order Management
  - Notifications
  - Payment Integration (future-ready)

## 🛠️ Tech Stack

| Layer       | Technology               |
|-------------|---------------------------|
| Frontend    | React.js, Tailwind CSS, Axios |
| Backend     | Spring Boot (Java), Node.js (Express.js) |
| Database    | MongoDB (product catalog), PostgreSQL (user & order data) |
| Auth        | Spring Security, JWT |
| Communication | REST APIs, Internal API Gateway |
| Deployment  | Docker, Docker Compose, Azure DevOps |
| Storage     | Azure Blob Storage (for product images/docs) |

## 📷 Screenshots

_Add screenshots or demo GIFs here to showcase the UI._

## 🚀 Getting Started

### Prerequisites

- Node.js
- Java 17+
- MongoDB & PostgreSQL instances
- Docker & Docker Compose

### Clone and Run

```bash
# Clone the monorepo
git clone https://github.com/Pritesh-Kadam/tanishka-ecommerce.git
cd tanishka-ecommerce

# Run services using Docker Compose
docker-compose up --build
