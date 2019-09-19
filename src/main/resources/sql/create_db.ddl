CREATE DATABASE IF NOT EXISTS wallet_cache;

USE wallet_cache;

CREATE TABLE "smart_contract" (
	"address"	TEXT NOT NULL UNIQUE,
	"deployer"	TEXT NOT NULL,
	"source_code"	TEXT NOT NULL,
	"time_stamp_creation"	TEXT NOT NULL,
	"state"	BLOB,
	"bytecode"	BLOB,
	PRIMARY KEY("address")
)

CREATE TABLE "transaction" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"inner_id"	INTEGER NOT NULL,
	"sender"	TEXT,
	"receiver"	TEXT,
	"block_num"	TEXT,
	"time_stamp"	TEXT,
	"user_data"	TEXT,
	"smart_contract_id"	INTEGER
)