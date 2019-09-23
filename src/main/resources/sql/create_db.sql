BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "method" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	INTEGER,
	"return_type_id"	INTEGER,
	FOREIGN KEY("return_type_id") REFERENCES "argument"("id"),
	FOREIGN KEY("name") REFERENCES "method_name"("name")
);
CREATE TABLE IF NOT EXISTS "argument_value" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"argument_id"	INTEGER NOT NULL,
	"value"	TEXT,
	FOREIGN KEY("argument_id") REFERENCES "argument"("id")
);
CREATE TABLE IF NOT EXISTS "type" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"name"	TEXT
);
CREATE TABLE IF NOT EXISTS "transaction_has_contract_call" (
	"transaction_id"	INTEGER NOT NULL,
	"contract_call_id"	INTEGER NOT NULL,
	FOREIGN KEY("transaction_id") REFERENCES "transaction"("id"),
	FOREIGN KEY("contract_call_id") REFERENCES "contract_call"("id")
);
CREATE TABLE IF NOT EXISTS "transaction" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"inner_id"	INTEGER,
	"sender"	TEXT,
	"receiver"	TEXT,
	"amount"	TEXT,
	"fee"	TEXT,
	"user_data"	TEXT,
	FOREIGN KEY("receiver") REFERENCES "wallet"("address"),
	FOREIGN KEY("sender") REFERENCES "wallet"("address")
);
CREATE TABLE IF NOT EXISTS "contract_has_method" (
	"contract_id"	INTEGER NOT NULL,
	"method_id"	INTEGER NOT NULL,
	FOREIGN KEY("contract_id") REFERENCES "smart_contract"("address"),
	FOREIGN KEY("method_id") REFERENCES "method"("id")
);
CREATE TABLE IF NOT EXISTS "contract_call" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"contract_address"	TEXT NOT NULL,
	"initiator"	TEXT NOT NULL,
	"method"	INTEGER,
	FOREIGN KEY("contract_address") REFERENCES "wallet"("address"),
	FOREIGN KEY("initiator") REFERENCES "wallet"("address"),
	FOREIGN KEY("method") REFERENCES "method"("id")
);
CREATE TABLE IF NOT EXISTS "smart_contract" (
	"address"	TEXT NOT NULL UNIQUE,
	"source_code"	INTEGER NOT NULL,
	FOREIGN KEY("address") REFERENCES "wallet"("address"),
	PRIMARY KEY("address")
);
CREATE TABLE IF NOT EXISTS "wallet" (
	"address"	TEXT NOT NULL UNIQUE,
	PRIMARY KEY("address")
);
CREATE TABLE IF NOT EXISTS "argument" (
	"id"	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,
	"type_id"	INTEGER,
	"name"	TEXT,
	FOREIGN KEY("type_id") REFERENCES "type"("id")
);
CREATE TABLE IF NOT EXISTS "method_has_argument" (
	"method_id"	INTEGER NOT NULL,
	"argument_id"	INTEGER NOT NULL,
	FOREIGN KEY("argument_id") REFERENCES "argument"("id"),
	FOREIGN KEY("method_id") REFERENCES "method"("id")
);
CREATE TABLE IF NOT EXISTS "method_name" (
	"name"	TEXT NOT NULL UNIQUE,
	PRIMARY KEY("name")
);
COMMIT;
