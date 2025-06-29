-- V9__Remove_Manufacturer_Table_And_References.sql
-- Drops the manufacturers table and removes manufacturer_id from car_models and any other references

-- Remove manufacturer_id column from car_models
ALTER TABLE car_models DROP COLUMN IF EXISTS manufacturer_id;

-- Drop the manufacturers table
DROP TABLE IF EXISTS manufacturers;