# BookWorm

A simple app for managing reading groups and logging reading activities.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)

## Introduction

BookWorm is a simple application designed to help users manage their reading groups and logging reading activities. It allows users to create groups, add members, and track their reading progress.

## Features

- Create and manage groups
- Add and remove members
- Track reading activities
- Promote members to different roles
- Manage waitlists

## Installation

To install and run the BookWorm application, follow these steps:

1. Install Java-17 Open JDK and Maven 3.8.8 locally
1. Clone the repository:

   ```sh
   git clone https://github.com/lsong44/BookWorm.git
   cd BookWorm/mainProj
   ```
1. Build the project using Maven
  ```sh
  mvn clean install
  ```
1. Run the applicaion
  ```sh
  java -jar target/bookworm-0.0.1-SNAPSHOT.jar
  ```

## Usage
Once the application is running, you can access the API endpoints to manage your reading activities.

### Accessing Swagger UI
You can access the Swagger UI to interact with the API endpoints:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints
Here are some of the main API endpoints available in the BookWorm application:

### Group Management
- Create Group: POST /api/group/register
- Delete Group: POST /api/group/delete
- Edit Group: PUT /api/group/edit

### Membership Management
- Promote Membership: POST /api/promote-membership
- Manage Waitlist: POST /api/manage-waitlist
- Clean Up Membership: POST /api/membership/clean-up
- Batch Promote Membership for a given group: POST /api/membership/promote/batch

### Example Requests
#### Create Group
```sh
curl -X POST "http://localhost:8080/api/group/register" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "name=GroupA"
 ```

#### Promote Membership
```sh
curl -X POST "http://localhost:8080/api/promote-membership" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "memberName=JohnDoe&groupName=GroupA"
```

## Contributing
Contributions are welcome! Please fork the repository and submit a pull request with your changes.

1. Fork the repository
1. Create a new branch (git checkout -b feature-branch)
1. Make your changes
1. Commit your changes (git commit -am 'Add new feature')
1. Push to the branch (git push origin feature-branch)
1. Create a new Pull Request

## License
This project is licensed under the MIT License. See the LICENSE file for details.