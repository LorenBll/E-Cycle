# E-Cycle

Marketplace platform for reselling unused goods through characteristic-based matching between requests and offers.

## Table of contents

- [Why this project](#why-this-project)
- [Features](#features)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Installation](#installation)
- [Usage](#usage)
- [Screenshots](#screenshots)
- [Notes](#notes)
- [Developer](#developer)

## Why this project

Traditional marketplaces for unused goods are fragmented and lack structural logic. Supply and demand struggle to meet because there's no dedicated space where users can search for goods based on specific characteristics rather than exact product names. This creates inefficiency in the reuse economy.

E-Cycle solves this by providing a characteristic-based matching system that lets users offer goods they no longer need and search for items based on desired features, automatically pairing compatible requests and offers while facilitating negotiations with minimal effort.

## Features

- User registration and secure authentication
- Profile management with personal information
- Request creation with characteristic-based specifications
- Offer creation with detailed product information
- Automatic matching between compatible requests and offers
- Price-range-aware matching algorithm
- Negotiation interface between matched parties
- Accept/reject decisions with clear feedback
- Advanced filtering by category, nature, brand, and characteristics
- Category browsing

## Tech stack

- Java 24
- Spring Boot
- Spring Web (MVC + REST)
- Spring Data JPA / Hibernate
- Thymeleaf
- MySQL
- Maven
- Lombok

## Project structure

```text
.
├─ src/
│  └─ main/
│     ├─ java/ecycle/ecycle/
│     │  ├─ controllers/           # MVC and REST controllers
│     │  ├─ models/                # JPA entities and request bodies
│     │  ├─ repositories/          # Spring Data repositories
│     │  ├─ services/              # Business logic
│     │  └─ EcycleApplication.java # Application entry point
│     └─ resources/
│        ├─ static/
│        │  ├─ css/                # Stylesheets
│        │  └─ js/                 # Client-side scripts
│        ├─ templates/             # Thymeleaf HTML templates
│        └─ application.properties
├─ docs/
│  └─ images/                      # UI screenshots
├─ ecycle-empty.sql                 # Database initialisation script
├─ pom.xml                          # Maven build configuration
├─ LICENSE
└─ README.md
```

The project follows a standard Spring Boot layout:
- **controllers/**: Handles HTTP requests and view routing
- **models/**: JPA entities and form/request body classes
- **repositories/**: Database access interfaces
- **services/**: Core business logic and matching algorithm

## Installation

### Prerequisites

- Java 24 or newer
- Maven 3.6 or newer
- MySQL 8.0 or newer

### Quick start

1. Clone the repository:
   ```bash
   git clone https://github.com/LorenBll/E-Cycle.git
   cd E-Cycle
   ```

2. Create the database and import the schema:
   ```bash
   mysql -u [username] -p -e "CREATE DATABASE ecycle;"
   mysql -u [username] -p ecycle < ecycle-empty.sql
   ```

3. Configure the database connection:
   Edit `src/main/resources/application.properties` with your credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ecycle
   spring.datasource.username=[your-username]
   spring.datasource.password=[your-password]
   ```

4. Build and run:
   ```bash
   mvn spring-boot:run
   ```

5. Open your browser and navigate to `http://localhost:8080`

### Manual execution

1. Verify prerequisites:
   ```bash
   java -version
   mvn -version
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Usage

1. Register a new account and log in
2. Complete your profile with personal details
3. Post a request describing what you are looking for with specific characteristics
4. Post an offer listing goods you want to resell
5. Browse automatically generated matches between your requests and offers
6. Start negotiations with matched parties and accept or reject proposals

## Screenshots

![Login Screen](docs/images/login.png)

![Registration Screen](docs/images/registration.png)

![Profile Edit](docs/images/profile-edit.png)

![Request Insertion](docs/images/requestInsertion.png)

![Offer Insertion](docs/images/offerInsertion.png)

![Request Details](docs/images/requestDetails.png)

![Offer Details](docs/images/offerDetails.png)

## Notes

- E-Cycle is a school project developed in under two weeks alongside several exam preparations. It is not intended for production use and should not be taken as an example of programming best practices.

## Developer

Created by [LorenBll](https://github.com/LorenBll)
