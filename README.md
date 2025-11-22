
# Analisador de Currículo com IA

Projeto Spring que  cria vagas, recebe aplicações a essa vaga com um curriculo em pdf, da matches em conhecimentos e retorna uma resposta elaborada e estruturada para o recrutador e aplicante.




## Tecnologias utilizadas

**Api:** Java, Spring;

**Autenticação:** Spring Security, token JWT;

**Banco de dados:** PostgreSql, flyway para migrações;

**AI de integração:** Gemini do Google





## Documentação da API

#### Criar uma vaga
POST /vacancy

| Body | Descrição |
| :--- | :-------- |
| VacancyRequestDto | Obrigatório. Requer autenticação |

#### Retornar todas as vagas
GET /vacancy

| retorno | Descrição |
| :--- | :-------- |
| List<VacancyResponseDto> | Obrigatório. Requer autenticação |

#### Retornar uma vaga específica
GET /vacancy/{vacancyId}

| Parâmetro | Tipo | Descrição |
| :-------- | :--- | :-------- |
| vacancyId | Long | Obrigatório. ID da vaga e requer autenticação |

#### Editar uma vaga
PUT /vacancy/{vacancyId}

| Parâmetro | Tipo | Descrição |
| :-------- | :--- | :-------- |
| vacancyId | Long | Obrigatório. ID da vaga a ser editada |
| Body | VacancyRequestDto | Obrigatório. Dados da vaga e autenticação |

#### Aplicar para uma vaga
POST /vacancy/application/{vacancyId}

| Parâmetro | Tipo | Descrição |
| :-------- | :--- | :-------- |
| vacancyId | Long | Obrigatório. ID da vaga para aplicar |
| file | MultipartFile | Obrigatório. Arquivo de currículo anexado |
| Autenticação | - | Usuário deve estar autenticado |

#### Ver análise da IA (aplicante ou recrutador)
GET /ai/{analysisId}

| Parâmetro | Tipo | Descrição |
| :-------- | :--- | :-------- |
| analysisId | Long | Obrigatório. ID da análise. Apenas o recrutador ou o candidato podem acessar |

#### Retornar todas as análises de um usuário
GET /vacancy/my

| Tipo | Descrição |
| :--- | :-------- |
| AiResponseEntity | Obrigatório. Requer autenticação e permissões adequadas |

#### Excluir uma vaga
DELETE /vacancy/{vacancyId}

| Parâmetro | Tipo | Descrição |
| :-------- | :--- | :-------- |
| vacancyId | Long | Obrigatório. Requer autenticação e permissões de recrutador |
## Rodando localmente

Clone o projeto

```bash
  git clone https://github.com/andreluisx/Resume-IA-analysis.git
```

Entre no diretório do projeto

```bash
  cd Resume-IA-analysis
```

Criando o banco de dados

```bash
  crie o banco de dados de sua preferencia
```

Altere as variaveis de ambiente

```bash
  crie .env com base em .env-example
```

Instale as dependências

```bash
  ./mvnw clean install
```
caso esteja no windows:
```bash
   mvn clean install
```

Inicie o servidor

```bash
  ./mvnw spring-boot:run
```
caso esteja no windows:
```bash
   mvn spring-boot:run
```


## Autores

- [@andreluisx](https://www.github.com/octokatherine)
- [@andre_luissx](https://www.instagram.com/andre_luissx)
