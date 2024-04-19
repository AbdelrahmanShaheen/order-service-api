CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    coupon_code TEXT,
    user_email TEXT NOT NULL,
    total_price FLOAT NOT NULL,
    total_price_after_discount FLOAT NOT NULL,
    creation_date TIMESTAMP NOT NULL,
    order_code TEXT NULL
);
CREATE TABLE "order_item" (
   id SERIAL PRIMARY KEY,
   order_id INT REFERENCES "order"(id),
   product_code TEXT NOT NULL,
   quantity INT NOT NULL
);

