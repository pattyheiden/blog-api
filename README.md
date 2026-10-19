# Blog API

API REST didática para gerenciamento de posts e comentários, desenvolvida com Java, Spring Boot e MySQL.

> **Em desenvolvimento:** funcionalidades, contratos de resposta e estrutura do projeto podem mudar. Este README descreve o código atual e os pontos que ainda precisam de ajustes; a aplicação ainda não é apresentada como uma versão pronta para produção.

## Recursos presentes no código

- Criação, consulta, edição e exclusão de posts.
- Listagem paginada, ordenação e busca por parte do título, sem diferenciar maiúsculas e minúsculas.
- Campo opcional de categoria para posts.
- Criação, edição e exclusão de comentários vinculados a posts.
- Identificadores UUID e datas de criação geradas pela aplicação.
- Persistência com Spring Data JPA e MySQL.
- Versionamento do banco de dados com Flyway.
- DTOs, mapeadores e validação dos dados de entrada.
- Tratamento centralizado de erros de validação e recursos não encontrados.
- Estrutura de mensagens em português, inglês e espanhol.
- Documentação OpenAPI e Swagger UI.
- Testes unitários do serviço com JUnit e Mockito.

## Tecnologias

| Tecnologia | Versão ou finalidade |
| --- | --- |
| Java | 21 |
| Spring Boot | 3.2.4 |
| Spring Web | Endpoints REST |
| Spring Data JPA / Hibernate | Persistência e relacionamentos |
| MySQL | Banco de dados relacional |
| Flyway | Migrações do esquema |
| Jakarta Bean Validation | Validação dos DTOs |
| Springdoc OpenAPI | 2.3.0 |
| JUnit Jupiter e Mockito | Testes automatizados |
| Maven Wrapper | Build e gerenciamento de dependências |

## Executar localmente

### 1. Pré-requisitos e clone

- JDK 21, com `JAVA_HOME` configurado.
- MySQL em execução e um usuário com permissões para criar e alterar as tabelas do projeto.
- Git e acesso à internet para baixar as dependências na primeira execução.

```bash
git clone https://github.com/pattyheiden/blog-api.git
cd blog-api
```

O Maven Wrapper está incluído no repositório; não é necessário instalar o Maven separadamente.

### 2. Ajuste pendente no POM

Na versão documentada, o `pom.xml` contém os textos `// Mudar` após as declarações de `groupId` e `artifactId`. Essa sintaxe não é um comentário XML e deve ser removida ou substituída por `<!-- Mudar -->` antes de executar o Maven.

As declarações devem ficar assim:

```xml
<groupId>com.lima</groupId>
<artifactId>blog-api</artifactId>
```

### 3. Configurar o banco

O arquivo `src/main/resources/application.properties` aponta para o banco `blogappdb` em `localhost`, com usuário `root` e senha vazia. Configure as credenciais do seu ambiente antes de iniciar.

É possível sobrescrever a configuração por variáveis de ambiente. No PowerShell, na mesma sessão em que a aplicação será iniciada:

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:mysql://localhost:3306/blogappdb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Sao_Paulo'
$env:SPRING_DATASOURCE_USERNAME = 'seu_usuario'
$env:SPRING_DATASOURCE_PASSWORD = 'sua_senha'
```

No Linux ou macOS:

```bash
export SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/blogappdb?createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Sao_Paulo'
export SPRING_DATASOURCE_USERNAME='seu_usuario'
export SPRING_DATASOURCE_PASSWORD='sua_senha'
```

A opção `createDatabaseIfNotExist=true` depende da permissão do usuário para criar o banco. Caso ele não tenha essa permissão, crie `blogappdb` previamente. A URL acima é destinada ao ambiente local de desenvolvimento.

### 4. Iniciar a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Após a inicialização bem-sucedida, os endereços padrão são:

- **Base da API:** `http://localhost:8080/api`
- **Swagger UI:** [Abrir documentação](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [Abrir especificação](http://localhost:8080/v3/api-docs)

## Banco de dados e migrations

O Flyway executa as migrations de `src/main/resources/db/migration`. O Hibernate está configurado com `ddl-auto=validate`, para validar o esquema em vez de atualizá-lo automaticamente.

| Migration | Alteração |
| --- | --- |
| `V1__criacao_tabela_blog.sql` | Cria `tb_post` |
| `V2__criar_tabela_comentarios.sql` | Cria `tb_comentario` e a chave estrangeira para o post |
| `V3__adicionar_coluna_categoria.sql` | Adiciona a coluna opcional `categoria` |

Um post pode possuir vários comentários. A relação possui exclusão em cascata tanto no mapeamento JPA quanto na chave estrangeira definida pela migration. Os UUIDs são armazenados como `BINARY(16)`.

O projeto configura `baseline-on-migrate=true` e `baseline-version=1`. Em um banco existente e não vazio, sem histórico do Flyway, essa configuração pressupõe que a estrutura da versão 1 já está presente. Para uma primeira execução, utilize um banco novo dedicado ao projeto.

A carga de exemplos em `DataUtils` não é executada automaticamente, pois sua anotação `@PostConstruct` está comentada.

## Endpoints

Os parâmetros `id`, `postId` e `comentarioId` representam UUIDs.

| Método | Rota | Operação | Sucesso |
| --- | --- | --- | --- |
| `GET` | `/api/posts` | Lista posts com paginação e filtro opcional | `200` |
| `GET` | `/api/posts/{id}` | Consulta um post com seus comentários | `200` |
| `POST` | `/api/newpost` | Cria um post | `201` |
| `PUT` | `/api/posts/{id}` | Atualiza um post | `200` |
| `DELETE` | `/api/posts/{id}` | Exclui um post | `204` |
| `POST` | `/api/comentarios/{postId}` | Adiciona um comentário a um post | `201` |
| `PUT` | `/api/comentarios/{comentarioId}` | Atualiza um comentário | `200` |
| `DELETE` | `/api/comentarios/{comentarioId}` | Exclui um comentário | `204` |
| `GET` | `/api/contexto` | Demonstra formatação por idioma e região | `200` |

Não há uma rota independente de listagem de comentários; eles fazem parte da resposta dos posts. As exclusões bem-sucedidas não retornam corpo.

### Paginação, ordenação e busca

Exemplo de requisição:

```http
GET /api/posts?page=0&size=5&sortBy=data&direction=desc&titulo=spring
```

| Parâmetro | Padrão | Descrição |
| --- | --- | --- |
| `page` | `0` | Índice da página, começando em zero |
| `size` | `5` | Quantidade de posts por página |
| `sortBy` | `data` | Propriedade de ordenação, como `data` ou `titulo` |
| `direction` | `desc` | `asc` para crescente; os demais valores usam decrescente na implementação atual |
| `titulo` | Ausente | Busca por trecho do título; vazio ou em branco não aplica o filtro |

A resposta é um `Page<PostResponseDto>`, com os posts em `content` e metadados de paginação, como `totalElements` e `totalPages`.

### Criar ou atualizar um post

Envie o cabeçalho `Content-Type: application/json`. O corpo abaixo é utilizado em `POST /api/newpost` e `PUT /api/posts/{id}`:

```json
{
  "autor": "Patricia",
  "titulo": "Introdução ao Spring Boot",
  "texto": "Meu primeiro post sobre desenvolvimento de APIs.",
  "categoria": "Tecnologia"
}
```

| Campo | Regra |
| --- | --- |
| `autor` | Obrigatório, não pode conter apenas espaços, máximo de 70 caracteres |
| `titulo` | Obrigatório, não pode conter apenas espaços, máximo de 100 caracteres |
| `texto` | Obrigatório, não pode conter apenas espaços |
| `categoria` | Opcional, máximo de 50 caracteres |

O `PUT` recebe os dados completos do post. Omitir `categoria` atribui `null` ao campo. ID e data são gerados na criação e não fazem parte do DTO de entrada.

Exemplo ilustrativo de resposta de criação:

```json
{
  "id": "b7898468-7f01-4c8c-880c-1f44a48b8ce2",
  "autor": "Patricia",
  "data": "2026-10-19",
  "titulo": "Introdução ao Spring Boot",
  "texto": "Meu primeiro post sobre desenvolvimento de APIs.",
  "categoria": "Tecnologia",
  "comentarios": []
}
```

Valores nulos são omitidos nas respostas pela configuração do Jackson.

### Criar ou atualizar um comentário

Utilize `POST /api/comentarios/{postId}` para criar ou `PUT /api/comentarios/{comentarioId}` para editar:

```json
{
  "comentario": "Ótima explicação sobre o assunto!"
}
```

O campo `comentario` é obrigatório e não pode conter apenas espaços. A resposta contém `id`, `data` e `comentario`.

## Idiomas e contexto regional

O projeto configura os idiomas `pt-BR` (padrão), `en-US` e `es-ES` por meio do cabeçalho `Accept-Language`:

```http
GET /api/contexto
Accept-Language: en-US
```

O endpoint demonstrativo retorna `locale`, `timezone`, `data`, `moeda` e `numero`. Ele utiliza valores de exemplo para mostrar a formatação regional, com os fusos `America/Sao_Paulo`, `America/New_York` e `Europe/Madrid` conforme a região.

Há arquivos de mensagens nos três idiomas. A internacionalização ainda está em desenvolvimento: a mensagem geral de validação e a validação de tamanho da categoria permanecem fixas em português.

## Tratamento de erros

O tratamento centralizado define respostas para `ResourceNotFoundException` (`404`) e falhas de validação dos DTOs (`400`).

Exemplo ilustrativo de erro de validação:

```json
{
  "timestamp": "2026-10-19T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Erro de validação.",
  "fieldErrors": {
    "autor": "O autor é obrigatório"
  }
}
```

Os erros de recurso não encontrado usam o mesmo formato, com `status: 404`, `error: "Not Found"` e `fieldErrors: {}`. O tratamento ainda não cobre todos os cenários, como a tentativa de adicionar um comentário a um post inexistente.

## Testes

Após o ajuste do `pom.xml`, execute os testes unitários do serviço, que utilizam mocks e não precisam de MySQL:

```powershell
# Windows
.\mvnw.cmd "-Dtest=PostServiceImplTest" test
```

```bash
# Linux ou macOS
./mvnw -Dtest=PostServiceImplTest test
```

Os quatro testes atuais verificam filtro por título e mapeamento, consulta de post inexistente, edição de post e exclusão de comentário.

Para executar a suíte completa:

```powershell
# Windows
.\mvnw.cmd test
```

```bash
# Linux ou macOS
./mvnw test
```

A suíte também possui `BlogApiApplicationTests`, com `@SpringBootTest`. Esse teste carrega o contexto e depende da configuração do banco e das migrations. Os relatórios são gerados em `target/surefire-reports/`.

## Estrutura do projeto

```text
src/main/
├── java/com/api/blogapi/
│   ├── config/           # OpenAPI e configuração de mensagens
│   ├── controller/       # Rotas de posts e comentários
│   ├── dto/              # Contratos de entrada e saída
│   ├── exception/        # Exceções e tratamento de erros
│   ├── mapper/           # Conversão entre entidades e DTOs
│   ├── model/            # Entidades JPA
│   ├── repository/       # Acesso ao banco
│   ├── service/          # Regras de negócio e mensagens
│   ├── teste/context/    # Demonstração de contexto regional
│   ├── utils/            # Dados de exemplo
│   └── BlogApiApplication.java
└── resources/
    ├── db/migration/     # Migrations Flyway
    ├── application.properties
    └── messages*.properties

src/test/java/com/api/blogapi/
├── BlogApiApplicationTests.java
└── PostServiceImplTest.java
```



