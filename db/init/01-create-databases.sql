-- Executado uma unica vez pelo entrypoint do Postgres (data dir vazio).
-- Cria um banco isolado por aplicacao: assim cada app pode usar
-- drop-and-create/update sem colidir com a tabela `contatos` da outra API.
--
-- O owner por padrao e o proprio POSTGRES_USER (superuser criado pela imagem).

CREATE DATABASE contatos_spring;
CREATE DATABASE contatos_quarkus;
