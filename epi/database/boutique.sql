-- phpMyAdmin SQL Dump
-- version 3.5.7
-- http://www.phpmyadmin.net
--
-- Client: localhost
-- Généré le: Jeu 30 Mai 2013 à 09:26
-- Version du serveur: 5.5.29
-- Version de PHP: 5.4.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Base de données: `symfony`
--

-- --------------------------------------------------------

--
-- Structure de la table `CategorieProduit`
--

--
-- Contenu de la table `CategorieProduit`
--

INSERT INTO `CategorieProduit` (`id`, `nom`) VALUES
(1, 'Frais'),
(2, 'Epicerie salée'),
(3, 'Epicerie sucrée'),
(4, 'Hygiène'),
(5, 'Produits d''entretien'),
(6, 'Autres');

-- --------------------------------------------------------

--
-- Contenu de la table `Panier`
--

INSERT INTO `Panier` (`id`, `user_id`) VALUES
(1, 1);

-- --------------------------------------------------------
--
-- Contenu de la table `Produit`
--

INSERT INTO `Produit` (`id`, `categorie_id`, `nom`, `description`, `quantite`, `prix`, `note`, `quantite_commandee`) VALUES
(1, 1, 'Lait', 'Pack de 6 litres, demi-écrémé', 2, 2.5, 0, 0),
(2, 2, 'Farine', 'Paquet de 1kg', 2, 1.5, 0, 0),
(3, 2, 'Riz', 'Paquet de 500g', 2, 0.5, 0, 0),
(4, 3, 'Chocolat', 'tablette de 200g', 2, 1.5, 0, 0),
(5, 5, 'Liquide vaisselle', 'PEC citron 500g', 2, 1.6, 0, 0);

-- --------------------------------------------------------
--
-- Contenu de la table `ProduitPanier`
--

INSERT INTO `ProduitPanier` (`id`, `produit_id`, `panier_id`, `quantite`) VALUES
(8, 1, 1, 3),
(9, 2, 1, 2);

-- --------------------------------------------------------
--
-- Contenu de la table `User`
--

INSERT INTO `User` (`id`, `username`, `password`, `salt`, `roles`) VALUES
(1, 'Lalou', 'Lalou', 'Lalou', 'N;');

