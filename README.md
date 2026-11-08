# Portal Saúde RH

Sistema web desenvolvido para apoiar a gestão de solicitações relacionadas ao plano de saúde dos colaboradores, proporcionando mais organização, transparência, rastreabilidade e agilidade na comunicação entre colaboradores e equipe responsável pelo atendimento.

## 1. Sobre o projeto

O Portal Saúde RH é um projeto acadêmico desenvolvido com o objetivo de digitalizar e organizar o processo de abertura e acompanhamento de solicitações relacionadas ao plano de saúde.

A aplicação centraliza as solicitações em um ambiente web, permitindo que os colaboradores registrem demandas, acompanhem seu andamento e interajam com a equipe responsável pelo atendimento.

Os perfis administrativos possuem funcionalidades para gerenciar solicitações, atualizar status, registrar respostas e consultar o histórico de movimentações.

## 2. Objetivos

- Centralizar as solicitações relacionadas ao plano de saúde.
- Facilitar a comunicação entre colaboradores e equipe responsável.
- Permitir o acompanhamento do andamento das solicitações.
- Manter o histórico de movimentações e respostas.
- Organizar documentos e anexos relacionados aos atendimentos.
- Melhorar a rastreabilidade e a gestão dos processos.
- Disponibilizar informações para acompanhamento gerencial.

## 3. Funcionalidades desenvolvidas

### 3.1 Área do colaborador

- Acesso ao portal mediante autenticação.
- Abertura de novas solicitações.
- Geração e consulta de protocolo.
- Consulta das próprias solicitações.
- Acompanhamento do status.
- Consulta ao histórico de movimentações.
- Visualização das respostas registradas pela equipe responsável.
- Envio de respostas e anexos conforme as regras do sistema.

### 3.2 Área administrativa

Os perfis ADM_PLANO_SAUDE e RH_GESTOR possuem funcionalidades administrativas, incluindo:

- Consulta e gerenciamento das solicitações.
- Visualização do histórico de atendimento.
- Atualização do status das solicitações.
- Registro de respostas aos colaboradores.
- Consulta aos documentos anexados.
- Gerenciamento de categorias.
- Acompanhamento de indicadores por meio do dashboard.
- Desativação de solicitações.

### 3.3 Histórico e comunicação

- Registro das movimentações das solicitações.
- Identificação do responsável por cada movimentação.
- Registro de respostas com data e horário.
- Possibilidade de anexar documentos às respostas.
- Comunicação entre colaborador e equipe responsável.

### 3.4 Assistente de inteligência artificial

O projeto contempla um assistente de inteligência artificial integrado à API da OpenAI, com o objetivo de apoiar o atendimento e oferecer orientações aos usuários.

A utilização desse recurso depende da configuração de uma chave de API válida por meio da variável de ambiente OPENAI_API_KEY.

## 4. Tecnologias utilizadas

| Tecnologia | Finalidade |
|---|---|
| Java | Linguagem de programação |
| Spring Boot | Framework para desenvolvimento da aplicação |
| Spring MVC | Organização e controle das requisições |
| Spring Data JPA | Persistência e acesso aos dados |
| Hibernate | Mapeamento objeto-relacional |
| Thymeleaf | Integração entre páginas HTML e aplicação Java |
| HTML e CSS | Estrutura e apresentação das interfaces |
| PostgreSQL | Banco de dados relacional |
| Maven | Gerenciamento de dependências e construção do projeto |
| JUnit e Mockito | Apoio aos testes automatizados |
| Git e GitHub | Versionamento e armazenamento do código-fonte |
| OpenAI API | Integração com o assistente de inteligência artificial |

## 5. Arquitetura da aplicação

O sistema utiliza o padrão arquitetural MVC (Model-View-Controller), organizado em camadas:

- **Model:** representa as entidades e os dados do sistema.
- **View:** apresenta as interfaces utilizadas pelos usuários, desenvolvidas com Thymeleaf e HTML.
- **Controller:** recebe as requisições e direciona as operações.
- **Service:** concentra as regras de negócio.
- **Repository:** realiza a comunicação com o banco de dados.

A separação dessas responsabilidades facilita a manutenção, a organização e a evolução da aplicação.

## 6. Pré-requisitos

Para executar o projeto localmente, são necessários:

- Java JDK compatível com a versão configurada no projeto.
- PostgreSQL instalado e em execução.
- Git instalado.
- Maven, caso não seja utilizado o Maven Wrapper disponibilizado no projeto.

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

Crie um banco de dados PostgreSQL para a aplicação:

```sql
CREATE DATABASE portal_saude_rh;
```

Configure as credenciais de acesso ao PostgreSQL por meio das variáveis de ambiente descritas na próxima seção.

O projeto utiliza o Hibernate com a propriedade:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Essa configuração permite que o Hibernate atualize a estrutura das tabelas conforme as entidades mapeadas na aplicação.

O arquivo `database/schema.sql` disponibiliza o script de estrutura do banco de dados como referência para criação das tabelas.

**Atenção:** antes de executar o script manualmente em um banco já existente, faça um backup e verifique a estrutura atual. O script não deve ser utilizado como mecanismo de atualização de uma base que já contenha dados.

## 9. Configuração das variáveis de ambiente

O projeto disponibiliza o arquivo `.env.example` como modelo das configurações necessárias.

Crie uma cópia:

```bash
cp .env.example .env
```

Configure os valores correspondentes ao ambiente local:

| Variável | Finalidade |
|---|---|
| DB_URL | Endereço de conexão com o PostgreSQL |
| DB_USERNAME | Usuário do banco de dados |
| DB_PASSWORD | Senha do banco de dados |
| PORTAL_ARQUIVOS_DIR | Diretório de armazenamento dos anexos |
| OPENAI_API_KEY | Chave de acesso à API de inteligência artificial |

**Importante:** o Spring Boot não carrega automaticamente as variáveis de um arquivo `.env`. Elas precisam estar disponíveis no ambiente de execução.

No Linux, macOS ou GitHub Codespaces, as variáveis podem ser disponibilizadas no terminal antes de iniciar a aplicação:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/portal_saude_rh"
export DB_USERNAME="portal_user"
export DB_PASSWORD="sua_senha"
export PORTAL_ARQUIVOS_DIR="./arquivos-privados"
```

Caso utilize o assistente de inteligência artificial, configure também a variável `OPENAI_API_KEY` no ambiente de execução.

O arquivo `.env` contém configurações privadas e não deve ser enviado ao GitHub. O `.env.example` deve conter somente valores fictícios ou campos vazios.

## 10. Como executar a aplicação

Após instalar os pré-requisitos, criar o banco de dados e disponibilizar as variáveis de ambiente, execute:

### Linux, macOS ou GitHub Codespaces

```bash
./mvnw spring-boot:run
```

### Windows

```bash
mvnw.cmd spring-boot:run
```

Após a inicialização, acesse pelo navegador:

```text
http://localhost:8080
```

## 11. Inicialização dos dados de demonstração

O projeto possui um serviço de inicialização chamado `InicializacaoService`, responsável por verificar e cadastrar usuários de demonstração quando a aplicação é iniciada, caso eles ainda não existam.

Os perfis contemplados são:

- COLABORADOR
- ADM_PLANO_SAUDE
- RH_GESTOR

As senhas são armazenadas utilizando o algoritmo BCrypt.

Os usuários de demonstração devem ser utilizados exclusivamente em ambientes acadêmicos, de desenvolvimento e testes. Para uma implantação real, devem ser adotadas credenciais individuais e configurações de segurança apropriadas.

## 12. Execução dos testes automatizados

O projeto possui testes automatizados para apoiar a validação das regras de negócio e do funcionamento da aplicação.

Para executar os testes:

### Linux, macOS ou GitHub Codespaces

```bash
./mvnw clean test
```

### Windows

```bash
mvnw.cmd clean test
```

A execução dos testes permite identificar possíveis falhas antes de novas alterações ou entregas do projeto.

## 13. Estrutura do projeto

```text
Portal-Saude-RH/
├── database/
│   └── schema.sql
├── arquivos-privados/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/portal/saudereh/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       ├── service/
│   │   │       ├── AssistenteIAController.java
│   │   │       ├── HomeController.java
│   │   │       ├── JacksonConfig.java
│   │   │       └── PortalSaudeRhApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   └── images/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── .env.example
├── .gitignore
├── HELP.md
├── README.md
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## 14. Repositório

Código-fonte disponível no GitHub:

https://github.com/HayChris23/Portal-Saude-RH

## 15. Considerações finais

O Portal Saúde RH representa uma proposta de modernização do processo de atendimento relacionado ao plano de saúde, utilizando tecnologias de desenvolvimento web para centralizar informações, organizar solicitações e facilitar a comunicação entre colaboradores e equipe responsável.

O projeto também possibilita futuras evoluções, como ampliação dos indicadores gerenciais, aprimoramento do dashboard e implementação de novos recursos de apoio inteligente ao atendimento.
