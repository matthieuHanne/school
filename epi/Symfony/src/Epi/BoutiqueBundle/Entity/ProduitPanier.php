<?php

namespace Epi\BoutiqueBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * ProduitPanier
 *
 * @ORM\Table()
 * @ORM\Entity
 */
class ProduitPanier
{
    /**
     * @var integer
     *
     * @ORM\Column(name="id", type="integer")
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="AUTO")
     */
    private $id;

    /**
     * @ORM\ManyToOne(targetEntity="Epi\BoutiqueBundle\Entity\Produit")
     * @ORM\JoinColumn(nullable=false)
     */
    private $produit;

    /**
     * @ORM\ManyToOne(targetEntity="Epi\BoutiqueBundle\Entity\Panier")
     * @ORM\JoinColumn(nullable=false)
     */
    private $panier;

    /**
     * @var integer
     *
     * @ORM\Column(name="quantite", type="integer")
     */
    private $quantite;


    public function __construct($produit, $panier, $quantite=0)
    {
        $this->produit = $produit;
        $this->panier = $panier;
        $this->quantite = $quantite;
    }


    /**
     * Get id
     *
     * @return integer 
     */
    public function getId()
    {
        return $this->id;
    }

    /**
     * Set produit
     *
     * @return Produit_panier
     */
    public function setProduit($produit)
    {
        $this->produit = $produit;
    
        return $this;
    }

    /**
     * Get produit
     *
     */
    public function getProduit()
    {
        return $this->produit;
    }

    /**
     * Set panier
     *
     * @return Produit_panier
     */
    public function setPanier($panier)
    {
        $this->panier = $panier;
    
        return $this;
    }

    /**
     * Get panier
     *
     */
    public function getPanier()
    {
        return $this->panier;
    }

    /**
     * Set quantite
     *
     * @param integer $quantite
     * @return Produit_panier
     */
    public function setQuantite($quantite)
    {
        $this->quantite = $quantite;
    
        return $this;
    }

    /**
     * Get quantite
     *
     * @return integer 
     */
    public function getQuantite()
    {
        return $this->quantite;
    }
}
