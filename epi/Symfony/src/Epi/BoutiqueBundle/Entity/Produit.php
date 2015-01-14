<?php

namespace Epi\BoutiqueBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Produit
 *
 * @ORM\Table()
 * @ORM\Entity(repositoryClass="Epi\BoutiqueBundle\Entity\ProduitRepository")
 */
class Produit
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
	 * @var string
	 *
	 * @ORM\Column(name="nom", type="string", length=255)
	 */
	private $nom;

	/**
	 * @var string
	 *
	 * @ORM\Column(name="description", type="string", length=4000)
	 */
	private $description;

	/**
	 * @var integer
	 *
	 * @ORM\Column(name="quantite", type="integer")
	 */
	private $quantite;

	/**
	 * @var float
	 *
	 * @ORM\Column(name="prix", type="float")
	 */
	private $prix;


	/**
	 * @var integer
	 *
	 * @ORM\Column(name="quantite_commandee", type="integer")
	 */
	private $quantiteCommandee;

	/**
	 * @ORM\ManyToOne(targetEntity="Epi\BoutiqueBundle\Entity\CategorieProduit")
	 * @ORM\JoinColumn(nullable=false)
	 */
	private $categorie;

	/**
	 * @var string
	 *
	 * @ORM\Column(name="image", type="string", length=4000, nullable = true)
	 */
	private $image;

	public function __construct()
	{
		$this->categorie = new \Doctrine\Common\Collections\ArrayCollection();
		$this->quantiteCommandee = 0;
	}

	public function upload()
	{
		if (null === $this->image)
			return;

		$name = $this->image->getClientOriginalName();
		$this->image = $name;
	}

	public function getUploadDir()
	{
		return 'img/products';
	}

	protected function getUploadRootDir()
	{
		return __DIR__.'/../../../web/'.$this->getUploadDir();
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
	 * Set nom
	 *
	 * @param string $nom
	 * @return Produit
	 */
	public function setNom($nom)
	{
		$this->nom = $nom;

		return $this;
	}

	/**
	 * Get nom
	 *
	 * @return string 
	 */
	public function getNom()
	{
		return $this->nom;
	}

	/**
	 * Set description
	 *
	 * @param string $description
	 * @return Produit
	 */
	public function setDescription($description)
	{
		$this->description = $description;

		return $this;
	}

	/**
	 * Get description
	 *
	 * @return string 
	 */
	public function getDescription()
	{
		return $this->description;
	}

	/**
	 * Set quantite
	 *
	 * @param integer $quantite
	 * @return Produit
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

	/**
	 * Set prix
	 *
	 * @param float $prix
	 * @return Produit
	 */
	public function setPrix($prix)
	{
		$this->prix = $prix;

		return $this;
	}

	/**
	 * Get prix
	 *
	 * @return float 
	 */
	public function getPrix()
	{
		return $this->prix;
	}


	/**
	 * Set quantiteCommandee
	 *
	 * @param integer $quantiteCommandee
	 * @return Produit
	 */
	public function setQuantiteCommandee($quantiteCommandee)
	{
		$this->quantiteCommandee = $quantiteCommandee;

		return $this;
	}

	/**
	 * Get quantiteCommandee
	 *
	 * @return integer 
	 */
	public function getQuantiteCommandee()
	{
		return $this->quantiteCommandee;
	}

	/**
	 * Set categorie
	 *
	 * @param CategorieProduit $categorie
	 * @return Produit
	 */
	public function setCategorie($categorie)
	{
		$this->categorie = $categorie;

		return $this;
	}

	/**
	 * Get categorie
	 *
	 * @return CategorieProduit 
	 */
	public function getCategorie()
	{
		return $this->categorie;
	}



    /**
     * Set image
     *
     * @param string $image
     * @return Produit
     */
    public function setImage($image)
    {
        $this->image = $image;
    
        return $this;
    }

    /**
     * Get image
     *
     * @return string 
     */
    public function getImage()
    {
        return $this->image;
    }
}
