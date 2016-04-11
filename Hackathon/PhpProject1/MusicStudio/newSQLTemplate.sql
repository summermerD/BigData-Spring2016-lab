/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Ting
 * Created: Apr 10, 2016
 */

-- Alter table Booking
-- Add Request_Time Timestamp;
-- 
-- ALTER TABLE Usr MODIFY Admininstrator INTEGER;

-- Update  Usr
-- Set Security_Answer='Anna'
-- Where
-- Usr_ID = 'j.jones';

-- delete from usr where usr_id = 1;
-- -- 
insert into booking (Booking_ID,Studio_ID, StartFrom, EndTo, Usr_ID, Status, Request_Time)values
(1, 1, 12300415,13300415, 'emma',1,1460);
-- 
-- ALTER TABLE booking MODIFY StartFrom Integer;
-- ALTER TABLE booking MODIFY EndTo Integer;


-- ALTER TABLE booking MODIFY Request_Time Integer;
