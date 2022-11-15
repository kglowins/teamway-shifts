CREATE SEQUENCE employee_id_seq;

CREATE TABLE employees (
  id        INTEGER NOT NULL DEFAULT nextval('employee_id_seq'),
  last_name TEXT NOT NULL,
  CONSTRAINT pk_employees PRIMARY KEY (id)
);

CREATE SEQUENCE shift_id_seq;

CREATE TABLE shifts (
    id INTEGER NOT NULL DEFAULT nextval('shift_id_seq'),
    employee_id BIGINT NOT NULL,
    shift_date DATE NOT NULL,
    shift_window VARCHAR(13) NOT NULL,
    CONSTRAINT pk_shifts PRIMARY KEY (id),
    FOREIGN KEY (employee_id) REFERENCES employees (id)
);

