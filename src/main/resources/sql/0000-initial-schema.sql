CREATE TABLE employees (
  id        SERIAL,
  last_name TEXT NOT NULL,
  CONSTRAINT pk_employees PRIMARY KEY (id)
);

CREATE TABLE shifts (
    id SERIAL,
    employee_id BIGINT NOT NULL,
    shift_date DATE NOT NULL,
    shift_window VARCHAR(13) NOT NULL,
    CONSTRAINT pk_shifts PRIMARY KEY (id),
    FOREIGN KEY (employee_id) REFERENCES employees (id)
);

