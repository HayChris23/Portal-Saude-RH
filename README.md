# Portal Saúde RH

Sistema web desenvolvido para apoiar a gestão de solicitações relacionadas ao plano de saúde dos colaboradores, proporcionando mais organização, transparência e agilidade na comunicação entre colaboradores e equipe responsável pelo atendimento.

## 1. Sobre o projeto

O Portal Saúde RH foi desenvolvido como projeto acadêmico com o objetivo de digitalizar e organizar o processo de abertura e acompanhamento de solicitações relacionadas ao plano de saúde.

A aplicação permite que os colaboradores registrem solicitações, acompanhem seu andamento e interajam com a equipe responsável. Os perfis administrativos podem gerenciar as solicitações, atualizar seus status e registrar respostas.

## 2. Objetivos

- Centralizar as solicitações relacionadas ao plano de saúde.
- Facilitar a comunicação entre colaboradores e equipe responsável.
- Permitir o acompanhamento do andamento das solicitações.
- Manter o histórico de movimentações e respostas.
- Organizar documentos e anexos relacionados aos atendimentos.
- Melhorar a rastreabilidade e a gestão dos processos.

## 3. Funcionalidades desenvolvidas

### Área do colaborador

- Acesso ao portal mediante login.
- Abertura de novas solicitações.
- Geração e consulta de protocolo.
- Consulta das próprias solicitações.
- Acompanhamento do status.
- Consulta ao histórico de movimentações.
- Visualização das respostas registradas pela equipe responsável.
- Envio de respostas e anexos enquanto a solicitação estiver pendente.

### Área administrativa (ADM_PLANO_SAUDE e RH_GESTOR)

- Consulta e gerenciamento das solicitações.
- Visualização do histórico de atendimento.
- Atualização do status das solicitações.
- Registro de respostas aos colaboradores.
- Consulta aos documentos anexados.
- Desativação de solicitações.

### Histórico e comunicação

- Registro das movimentações da solicitação.
- Identificação do responsável por cada movimentação.
- Registro de respostas com data e horário.
- Possibilidade de anexar documentos às respostas.
- Comunicação entre colaborador e equipe responsável.

## 4. Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java | Linguagem de programação |
| Spring Boot | Framework para desenvolvimento da aplicação |
| Spring MVC | Organização e controle das requisições |
| Thymeleaf | Integração entre páginas HTML e aplicação Java |
| HTML e CSS | Estrutura e apresentação das interfaces |
| Maven | Gerenciamento de dependências e construção do projeto |
| PostgreSQL | Banco de dados relacional |
| Git e GitHub | Versionamento e armazenamento do código-fonte |

## 5. Arquitetura da aplicação

O sistema utiliza o padrão arquitetural MVC (Model-View-Controller), organizando a aplicação em camadas:

- **Model:** representa as entidades e os dados do sistema.
- **View:** apresenta as interfaces utilizadas pelos usuários, com Thymeleaf e HTML.
- **Controller:** recebe as requisições e direciona as operações.
- **Service:** concentra as regras de negócio.
- **Repository:** realiza a comunicação com o banco de dados.

Essa organização facilita a manutenção, a evolução e a compreensão do código.

## 6. Pré-requisitos

Para executar o projeto localmente, é necessário possuir:

- Java JDK compatível com a versão configurada no projeto.
- PostgreSQL instalado e em execução.
- Git instalado.
- Maven (opcional, pois o projeto disponibiliza o Maven Wrapper).

## 7. Como obter o projeto

Clone o repositório:

```bash
git clone https://github.com/HayChris23/Portal-Saude-RH.git
```

Acesse a pasta:

```bash
cd Portal-Saude-RH
```

## 8. Configuração do banco de dados

Crie um banco de dados PostgreSQL para a aplicação.

Exemplo:

```sql
CREATE DATABASE portal_saude_rh;
```

Execute o script de estrutura disponibilizado na pasta `database`, conforme as instruções e a configuração do projeto.

É necessário configurar o usuário e a senha de acesso ao banco de dados.

## 9. Configuração das variáveis de ambiente

O projeto disponibiliza o arquivo `.env.example` como modelo para configuração das variáveis de ambiente.

Crie uma cópia com o nome `.env`:

```bash
cp .env.example .env
```

Configure os valores correspondentes ao ambiente local, utilizando as credenciais do PostgreSQL.

**Importante:** o arquivo `.env` contém configurações privadas e não deve ser enviado ao GitHub. Utilize o `.env.example` apenas como modelo, sem credenciais reais.

Caso utilize o assistente de inteligência artificial, configure também a variável de ambiente correspondente à chave da API, conforme a implementação do projeto.

## 10. Como executar a aplicação

Após configurar o banco de dados e as variáveis de ambiente, execute:

No Linux ou macOS:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Após a inicialização, acesse pelo navegador:

http://localhost:8080

## 11. Execução dos testes

Para executar os testes automatizados, utilize:

```bash
./mvnw test
```

No Windows:

```bash
mvnw.cmd test
```

Os testes verificam funcionalidades e regras de negócio implementadas no sistema.

## 12. Estrutura do projeto

```text
Portal-Saude-RH/
├── database/
│   └── schema.sql
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/portal/saudereh/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── .env.example
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 13. Repositório

GitHub: https://github.com/HayChris23/Portal-Saude-RH

## 14. Considerações finais

O Portal Saúde RH representa uma proposta de modernização do processo de atendimento relacionado ao plano de saúde, utilizando tecnologias de desenvolvimento web para centralizar informações, organizar solicitações e facilitar a comunicação entre colaboradores e equipe responsável.

O projeto também possibilita futuras evoluções, como ampliação dos indicadores gerenciais, aprimoramento do dashboard e implementação de recursos de apoio inteligente ao atendimento.
