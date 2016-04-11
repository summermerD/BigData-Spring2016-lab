CREATE TABLE IF NOT EXISTS Studio (
Studio_ID INT NOT NULL,
Room varchar(250) NOT NULL,
PRIMARY KEY(Studio_ID)
);

INSERT INTO Studio (Studio_ID, Room) VALUES
(1, 'Room1'),
(2, 'Room2');


/* Creating the Usr table */
CREATE TABLE IF NOT EXISTS Usr (
Usr_ID varchar(250) NOT NULL,
First_Name varchar(250) NOT NULL,
Last_Name varchar(250) NOT NULL,
Email varchar(250) NOT NULL,
Password varchar(250) NOT NULL,
Admininstrator Boolean,
Priority INT,

PRIMARY KEY(Usr_ID)
);

ALTER TABLE USR
Add Security_Question varchar(250) NOT NULL;

ALTER TABLE USR
Add Security_Answer varchar(250)NOT NULL;


INSERT INTO Usr (Usr_ID, First_Name, Last_Name, Email, Password, Admininstrator, Priority, Security_Question, Security_Answer) VALUES
('j.jones', 'Joseph', 'Jones', 'cs5542spring2016@gmail.com','ee7e4705dd4ac06adfe650c2cdc39bdd', 1,0, 'Best friend','Anna');

/* Creating the booking table */
CREATE TABLE IF NOT EXISTS Booking (
Booking_ID INT NOT NULL AUTO_INCREMENT,
Studio_ID INT NOT NULL,
StartFrom Time,
EndTo Time,
Usr_ID varchar(255) NOT NULL,
Status int(11) DEFAULT NULL,
PRIMARY KEY (Booking_ID),
FOREIGN KEY (Usr_ID) REFERENCES Usr(Usr_ID),
FOREIGN KEY (Studio_ID) REFERENCES Studio(Studio_ID)
);
