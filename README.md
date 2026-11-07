#Portal Saúde RH#
Sistema web desenvolvido para apoiar a gestão de solicitações relacionadas ao plano de saúde dos colaboradores, proporcionando mais organização, transparência e agilidade na comunicação entre colaboradores e equipe responsável pelo atendimento.

#Sobre o projeto#
O Portal Saúde RH foi desenvolvido como projeto acadêmico com o objetivo de digitalizar e organizar o processo de abertura e acompanhamento de solicitações relacionadas ao plano de saúde.
A aplicação permite que os colaboradores registrem solicitações, acompanhem seu andamento e interajam com a equipe responsável. Os perfis administrativos podem gerenciar as solicitações, atualizar seus status e registrar respostas.

#Objetivos#
* Centralizar as solicitações relacionadas ao plano de saúde.
* Facilitar a comunicação entre colaboradores e equipe responsável.
* Permitir o acompanhamento do andamento das solicitações.
* Manter o histórico de movimentações e respostas.
* Organizar documentos e anexos relacionados aos atendimentos.
* Melhorar a rastreabilidade e a gestão dos processos.

#Funcionalidades desenvolvidas#
-> Área do colaborador
* Acesso ao portal mediante login.
* Abertura de novas solicitações.
* Geração e consulta de protocolo.
* Consulta das próprias solicitações.
* Acompanhamento do status.
* Consulta ao histórico de movimentações.
* Visualização das respostas registradas pela equipe responsável.
* Envio de respostas e anexos enquanto a solicitação estiver pendente.

-> Área administrativa (Adm. do Plano e Gestor do RH)
* Consulta e gerenciamento das solicitações.
* Visualização do histórico de atendimento.
* Atualização do status das solicitações.
* Registro de respostas aos colaboradores.
* Consulta aos documentos anexados.
* Desativação de solicitações.

-> Histórico e comunicação
* Registro das movimentações da solicitação.
* Identificação do responsável por cada movimentação.
* Registro de respostas com data e horário.
* Possibilidade de anexar documentos às respostas.
* Comunicação entre colaborador e equipe responsável.

#Tecnologias utilizadas#
Java: linguagem de programação.
Spring Boot: framework para desenvolvimento da aplicação.
Spring MVC: organização e controle das requisições.
Thymeleaf: integração entre páginas HTML e aplicação Java.
HTML e CSS: estrutura e apresentação das interfaces.
Maven: gerenciamento de dependências e construção do projeto.
PostgreSQL: banco de dados relacional.
Git e GitHub: versionamento e armazenamento do código-fonte.

#Arquitetura da aplicação#
O sistema utiliza o padrão arquitetural MVC (Model-View-Controller), organizando a aplicação em camadas:
* Model: representa as entidades e os dados do sistema.
* View: apresenta as interfaces utilizadas pelos usuários, com Thymeleaf e HTML.
* Controller: recebe as requisições e direciona as operações.
* Service: concentra as regras de negócio.
* Repository: realiza a comunicação com o banco de dados.

Essa organização facilita a manutenção, a evolução e a compreensão do código.

#Como executar o projeto#
1 - Clone o repositório:

git clone https://github.com/HayChris23/Portal-Saude-RH.git

2 - Acesse a pasta do projeto:

cd Portal-Saude-RH

3 - Configure o banco de dados PostgreSQL conforme as propriedades definidas no projeto.

4 - Execute a aplicação utilizando Maven:

./mvnw spring-boot:run

5 - Acesse a aplicação pelo navegador, utilizando o endereço local configurado para o projeto:

http://localhost:8080

#Repositório# => GitHub: https://github.com/HayChris23/Portal-Saude-RH

#Considerações finais# 
O Portal Saúde RH representa uma proposta de modernização do processo de atendimento relacionado ao plano de saúde, utilizando tecnologias de desenvolvimento web para centralizar informações, organizar solicitações e facilitar a comunicação entre colaboradores e equipe responsável.
O projeto também possibilita futuras evoluções, como ampliação dos indicadores gerenciais, aprimoramento do dashboard e implementação de recursos de apoio inteligente ao atendimento.
