# Fullstack Java Project

## Maarten Claes 3AONc

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the databases, rabbitmq and frontend
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

Each folder contains its own specific `.gitignore` file.  
**:warning: complete these files asap, so you don't litter your repository with binary build artifacts!**

## How to setup and run this application

### Frontend:
Go to `/frontend-web/NewsArticles/` and execute `ng build`.
Go back to the root of the project and execute `docker compose up`.
After this you can go to `localhost:8001` in your browser.

### Backend
If you haven't done so already, do `docker compose up` in the root of the project
start the microservices in the following order:
- config-service
- discovery-service
- messaging-service
- All the rest of the microservices (order doesn't matter):
  -  gateway-service
  -  comment-service
  -  post-service
  -  review-service
