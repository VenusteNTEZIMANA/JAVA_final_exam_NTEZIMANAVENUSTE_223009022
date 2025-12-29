-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Dec 29, 2025 at 08:10 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `samsdb`
--

--
-- Dumping data for table `coaches`
--

INSERT INTO `coaches` (`CoachID`, `TeamID`, `Name`, `Identifier`, `Status`, `Location`, `Contact`, `AssignedSince`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 1, 'Cassius Mbungo', 'CMB001', 'Active', 'Kigali', '+250 78 111 0001', '2024-01-15', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 2, 'Musa Hassan', 'MHA002', 'Active', 'Nyanza', '+250 78 111 0002', '2023-08-20', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 3, 'Eric Nshimiyimana', 'ENS003', 'Active', 'Kigali', '+250 78 111 0003', '2024-02-01', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 4, 'Jean-Baptiste Kayiranga', 'JBK004', 'Active', 'Butare', '+250 78 111 0004', '2023-12-10', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 5, 'Vincent Mashami', 'VMA005', 'Active', 'Kigali', '+250 78 111 0005', '2024-03-05', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 6, 'Thierry Hitimana', 'THI006', 'Active', 'Gisenyi', '+250 78 111 0006', '2023-11-12', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 7, 'Sosthene Habimana', 'SHA007', 'Active', 'Kigali', '+250 78 111 0007', '2024-01-20', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 8, 'Emmanuel Ruremesha', 'ERU008', 'Active', 'Muhanga', '+250 78 111 0008', '2023-09-15', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 9, 'Felix Ndayisaba', 'FND009', 'Active', 'Kinihira', '+250 78 111 0009', '2024-02-28', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 10, 'Augustin Niyongabo', 'ANI010', 'Active', 'Nyamata', '+250 78 111 0010', '2023-10-07', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `equipment`
--

INSERT INTO `equipment` (`EquipmentID`, `Name`, `Quantity`, `Status`, `Location`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 'Nike Match Balls', 25, 'Good', 'Kigali Store', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 'Adidas Cones', 100, 'Good', 'Training Ground A', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 'Portable Goals', 4, 'Good', 'Butare Stadium', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 'GPS Vests', 20, 'Maintenance', 'Kigali Lab', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 'Heart-rate Straps', 30, 'Good', 'Medical Room', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 'Ice Bath Tubs', 2, 'Good', 'Recovery Centre', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 'Resistance Bands', 50, 'Good', 'Gym', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 'Ladders (Speed)', 10, 'Good', 'Store Room', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 'Water Coolers', 8, 'Good', 'Pitch Side', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 'Team Jerseys (Home)', 80, 'Good', 'Kit Room', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `matches`
--

INSERT INTO `matches` (`MatchID`, `HomeTeamID`, `AwayTeamID`, `MatchDate`, `Venue`, `CreatedAt`, `UpdatedAt`, `ScoreHome`, `ScoreAway`, `Winner`) VALUES
(1, 1, 2, '2025-03-15 15:30:00', 'Kigali Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 2, 1, 'Home'),
(2, 3, 4, '2025-03-16 15:30:00', 'Nyamirambo Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 1, 1, 'Draw'),
(3, 5, 6, '2025-03-17 15:30:00', 'Kicukiro Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 1, 'Away'),
(4, 7, 8, '2025-03-18 15:30:00', 'Umuganda Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 3, 0, 'Home'),
(5, 9, 10, '2025-03-22 15:30:00', 'Kinihira Ground', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 2, 2, 'Draw'),
(6, 2, 3, '2025-03-23 15:30:00', 'Nyanza Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 1, 0, 'Home'),
(7, 4, 5, '2025-03-24 15:30:00', 'Butare Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 1, 2, 'Away'),
(8, 6, 7, '2025-03-25 15:30:00', 'Gisenyi Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 0, 'Draw'),
(9, 8, 9, '2025-03-29 15:30:00', 'Muhanga Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 4, 1, 'Home'),
(10, 10, 1, '2025-03-30 15:30:00', 'Nyamata Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 1, 3, 'Away'),
(11, 1, 3, '2025-04-05 15:30:00', 'Kigali Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 2, 0, 'Home'),
(12, 2, 4, '2025-04-06 15:30:00', 'Nyanza Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 1, 'Away'),
(13, 5, 7, '2025-04-12 15:30:00', 'Kicukiro Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 0, 'Pending'),
(14, 6, 8, '2025-04-13 15:30:00', 'Gisenyi Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 0, 'Pending'),
(15, 9, 1, '2025-04-19 15:30:00', 'Kinihira Ground', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 0, 'Pending'),
(16, 10, 2, '2025-04-20 15:30:00', 'Nyamata Stadium', '2025-12-16 14:03:22', '2025-12-16 14:03:22', 0, 0, 'Pending');

--
-- Dumping data for table `match_players`
--

INSERT INTO `match_players` (`MatchID`, `PlayerID`, `TeamID`, `Starter`, `MinutesPlayed`) VALUES
(1, 1, 1, b'1', 90),
(1, 3, 1, b'1', 90),
(1, 4, 1, b'1', 82),
(6, 2, 2, b'1', 90),
(6, 7, 2, b'1', 90);

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`MessageID`, `SenderID`, `ReceiverID`, `Subject`, `Body`, `SentAt`) VALUES
(1, NULL, NULL, 'Welcome to SAMS', 'Your coach account is ready.', '2025-12-16 03:19:19'),
(2, NULL, NULL, 'Training reminder', 'Goalkeeping session at 8 AM.', '2025-12-16 03:19:19'),
(3, NULL, NULL, 'Match briefing', 'Line-up vs APR attached.', '2025-12-16 03:19:19'),
(4, NULL, NULL, 'Recovery data', 'My HRV readings for today.', '2025-12-16 03:19:19'),
(5, NULL, NULL, 'Diet question', 'Can I have extra carbs today?', '2025-12-16 03:19:19'),
(6, NULL, NULL, 'League update', 'New fair-play rules attached.', '2025-12-16 03:19:19'),
(7, NULL, NULL, 'Speed drill', 'Beat 4.0s in 30m tomorrow.', '2025-12-16 03:19:19'),
(8, NULL, NULL, 'Finishing drill', '100 shots after training.', '2025-12-16 03:19:19'),
(9, NULL, NULL, 'Distribution', 'Work on long throws.', '2025-12-16 03:19:19'),
(10, NULL, NULL, 'Counter session', 'Watch video of Rayon game.', '2025-12-16 03:19:19');

--
-- Dumping data for table `performances`
--

INSERT INTO `performances` (`PerformanceID`, `PlayerID`, `PerfDate`, `Metric`, `Value`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 1, '2025-03-01', 'Sprint time (30m)', '4.12s', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 2, '2025-03-01', 'Vertical jump', '68cm', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 3, '2025-03-02', 'Yo-Yo IR1', '20.8', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 4, '2025-03-02', 'Shot speed', '112 km/h', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 5, '2025-03-03', '5-min run', '1.28 km', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 6, '2025-03-03', 'Pass accuracy', '87 %', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 7, '2025-03-04', 'Agility t-test', '9.1s', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 8, '2025-03-04', 'Throw distance', '48m', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 9, '2025-03-05', 'Recovery HR', '118 bpm', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 10, '2025-03-05', 'Sprint repeat', '6.9s avg', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `players`
--

INSERT INTO `players` (`PlayerID`, `FullName`, `DOB`, `Position`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 'Jean-Bosco Ntaganda', '2000-04-12', 'Goalkeeper', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 'Eric Rutanga', '2001-07-03', 'Defender', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 'Olivier Karekezi', '1999-11-25', 'Midfielder', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 'Blaise Nizeyimana', '2002-01-18', 'Forward', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 'Patrick Mwumvaneza', '2000-09-09', 'Defender', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 'Alpha Niyomugabo', '2001-05-30', 'Midfielder', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 'Fiston Ndayisaba', '2003-02-22', 'Forward', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 'Didier Ndahiro', '1998-12-15', 'Goalkeeper', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 'Claude Niyomugabo', '2002-08-08', 'Midfielder', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 'Emery Ndayishimiye', '2001-06-20', 'Defender', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `scores`
--

INSERT INTO `scores` (`ScoreID`, `MatchID`, `PlayerID`, `Amount`, `ScoreDate`, `Type`, `Reference`, `Status`) VALUES
(11, 1, 1, 2, '2025-12-09 15:30:00', 'Goal', 'Normal play', 'Valid'),
(12, 2, 5, 1, '2025-12-10 18:45:00', 'Penalty', 'Foul in box', 'Valid'),
(13, 1, 3, 1, '2025-12-11 16:00:00', 'Goal', NULL, 'Valid'),
(14, 3, 2, 3, '2025-12-12 20:15:00', 'FreeThrow', 'Direct kick', 'Valid'),
(15, 2, 4, 1, '2025-12-13 14:20:00', 'OwnGoal', 'Deflection', 'Valid'),
(16, 1, 1, 2, '2025-12-14 17:10:00', 'Goal', 'Header', 'Valid'),
(17, 4, 6, 1, '2025-12-15 19:30:00', 'Penalty', NULL, 'Cancelled'),
(18, 2, 7, 4, '2025-12-16 16:45:00', 'Goal', 'Long shot', 'Valid'),
(19, 3, 2, 1, '2025-12-17 21:00:00', 'OwnGoal', NULL, 'Valid'),
(20, 1, 8, 5, '2025-12-18 15:55:00', 'Goal', 'Counter attack', 'Valid');

--
-- Dumping data for table `settings`
--

INSERT INTO `settings` (`SettingID`, `Skey`, `Svalue`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 'league_name', 'Rwanda Premier League 2025', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 'season_start', '2025-02-15', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 'season_end', '2025-11-30', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 'points_win', '3', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 'points_draw', '1', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 'points_loss', '0', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 'max_subs', '5', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 'game_duration', '90', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 'halftime_duration', '15', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 'currency', 'RWF', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `statistics`
--

INSERT INTO `statistics` (`StatisticID`, `MatchID`, `PlayerID`, `Amount`, `StatDate`, `Type`, `Reference`, `Status`) VALUES
(11, 1, 4, 1, '2025-03-15 16:45:00', 'Assist', 'Assist for first goal', 'Valid'),
(12, 1, 3, 1, '2025-03-15 17:20:00', 'YellowCard', 'Foul in midfield', 'Valid'),
(13, 6, 7, 1, '2025-03-23 15:55:00', 'Assist', 'Cross for winning goal', 'Valid'),
(14, 6, 2, 1, '2025-03-23 16:30:00', 'YellowCard', 'Tactical foul', 'Valid'),
(15, 4, 9, 2, '2025-03-18 16:10:00', 'Assist', 'Two assists in the match', 'Valid');

--
-- Dumping data for table `teams`
--

INSERT INTO `teams` (`TeamID`, `TeamName`, `sport`, `HomeLocation`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 'APR FC', 'football', 'Kigali', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 'Rayon Sports', 'football', 'Nyanza', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 'AS Kigali', 'football', 'Kigali', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 'Mukura VS', 'football', 'Butare', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 'Police FC', 'football', 'Kigali', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 'Etincelles', 'football', 'Gisenyi', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 'Gasogi United', 'football', 'Kigali', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 'Kiyovu Sports', 'football', 'Muhanga', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 'Sorwathe FC', 'football', 'Kinihira', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 'Bugesera FC', 'football', 'Nyamata', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `trainings`
--

INSERT INTO `trainings` (`TrainingID`, `PlayerID`, `TrainingDate`, `Activity`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 1, '2025-03-10', 'Goalkeeping drills', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(2, 2, '2025-03-10', 'Defensive shape', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(3, 3, '2025-03-11', 'Set-pieces', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(4, 4, '2025-03-11', 'Finishing', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(5, 5, '2025-03-12', 'Pressing circuit', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(6, 6, '2025-03-12', 'Possession rondo', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(7, 7, '2025-03-13', 'Speed & agility', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(8, 8, '2025-03-13', 'Distribution', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(9, 9, '2025-03-14', 'Counter-attack', '2025-12-16 05:19:19', '2025-12-16 05:19:19'),
(10, 10, '2025-03-14', 'Small-sided game', '2025-12-16 05:19:19', '2025-12-16 05:19:19');

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UserID`, `Username`, `Password`, `Role`, `CreatedAt`, `UpdatedAt`) VALUES
(1, 'jbntaganda', 'player123', 'PLAYER', '2025-12-27 09:56:12', '2025-12-27 09:56:12'),
(2, 'erutanga', 'player123', 'PLAYER', '2025-12-27 09:56:12', '2025-12-27 09:56:12'),
(3, 'okarekezi', 'player123', 'PLAYER', '2025-12-27 09:56:12', '2025-12-27 09:56:12'),
(4, 'bnizeyimana', 'player123', 'PLAYER', '2025-12-27 09:56:12', '2025-12-27 09:56:12'),
(5, 'pmwumvaneza', 'player123', 'PLAYER', '2025-12-27 09:56:12', '2025-12-27 09:56:12');
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
