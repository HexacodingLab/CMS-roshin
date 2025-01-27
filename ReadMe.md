# CMS Roshin

This Spring Boot application provides an API for managing articles using GraphQL. 
It supports querying and mutating article data, as well as an optional UI for visual management of articles.

## Features

- **GraphQL API** for managing articles.
    - Query to get articles by name.
    - Mutation to create and delete articles.
- **Article management UI** to visually manage articles.

## Getting Started

### Prerequisites

- Java 23+
- Gradle

### Running the Application

1. Clone the repository to your local machine:
    ```bash
    git clone https://github.com/HexacodingLab/CMS-roshin
    cd CMS-roshin
    ```

2. Build the application:
    ```bash
    ./gradlew clean build
    ```

3. Run the application:
    ```bash
    ./gradlew run
    ```

4. The application will be accessible at `http://localhost:8080`. You can access the GraphQL endpoint at `/api/graphql`.

5. Optionally, access the Playground for managing articles at `/api/graphiql`.

## Run Docker Image

1. Build the software
    ```bash
    docker compose up
    ```
   
    Or pull the latest build
    ```bash
    docker run -v "./database:/data" -p "8080:8080" ghcr.io/hexacodinglab/cms-roshin:latest
    ```

## Usage

### GraphQL Queries

You can query articles using the following GraphQL query:

```graphql
query {
  getArticles(name: "Article Name") {
    id
    name
    content
  }
}
```

If you do not provide the `name` argument, it will return all articles.

### GraphQL Mutations

To create a new article:

```graphql
mutation {
  createArticle(article: {name: "New Article", content: "Article content"}) {
    id
    name
    content
  }
}
```

To delete an article:

```graphql
mutation {
  deleteArticle(id: "article-id-here")
}
```

### Graphql Playground

Visit http://localhost:8080/api/graphiql?path=/api/graphql

You can manage articles visually at `/api/graphiql`. The UI allows you to:
- Query articles.
- Create new articles.
- Delete articles.

## Authentication

Basic Authentication is required to access the GraphQL mutations (create and delete operations) or the UI. 
You can use the credentials `admin:admin` for testing.

You configure a different user or password by setting the following environments variables:

- ROSHIN_USER=admin
- ROSHIN_PASSWORD=admin

## Technologies Used

- Spring Boot
- GraphQL
- Thymeleaf UI framework for visual management
