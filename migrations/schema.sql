--liquibase formatted sql


--changeset ocelot335:1
create table chats
(
    telegramId          bigint                  not null,

    constraint chats_pkey primary key (telegramId),
    unique (telegramId)
)
--rollback drop table chats;


--changeset ocelot335:2
create table links
(
    id              bigint generated always as identity,
    url             text                     not null,
    checked_at timestamp with time zone not null default NOW(),
    last_updated_at timestamp with time zone not null default NOW(),

    constraint links_pkey primary key (id),
    unique (url)
)
--rollback drop table links;

--changeset ocelot335:3
create table subscribes
(
    chatId              bigint                     not null,
    linkId              bigint                     not null,

    constraint subscribes_chatid_fkey foreign key(chatId) references chats(telegramId) on delete cascade,
    constraint subscribes_linkid_fkey foreign key(linkId) references links(id)
)
--rollback drop table subscribes;
