<?php

namespace Epi\CoreBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * News
 *
 * @ORM\Table()
 * @ORM\Entity(repositoryClass="Epi\CoreBundle\Entity\NewsRepository")
 */
class News
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
	 * @var \DateTime
	 *
	 * @ORM\Column(name="date", type="datetime")
	 */
	private $date;

	/**
	 * @var string
	 *
	 * @ORM\Column(name="title", type="string", length=255)
	 */
	private $title;

	/**
	 * @var string
	 *
	 * @ORM\Column(name="author", type="string", length=255)
	 */
	private $author;

	/**
	 * @var string
	 *
	 * @ORM\Column(name="contain", type="text")
	 */
	private $contain;


	/**
	 * @ORM\Column(name="publication", type="boolean")
	 *    */
	private $publication;

	public function __construct()
	{
		$this->date = new \Datetime();
		$this->publication = true;
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
	 * Set date
	 *
	 * @param \DateTime $date
	 * @return News
	 */
	public function setDate($date)
	{
		$this->date = $date;

		return $this;
	}

	/**
	 * Get date
	 *
	 * @return \DateTime 
	 */
	public function getDate()
	{
		return $this->date;
	}

	/**
	 * Set title
	 *
	 * @param string $title
	 * @return News
	 */
	public function setTitle($title)
	{
		$this->title = $title;

		return $this;
	}

	/**
	 * Get title
	 *
	 * @return string 
	 */
	public function getTitle()
	{
		return $this->title;
	}

	/**
	 * Set author
	 *
	 * @param string $author
	 * @return News
	 */
	public function setAuthor($author)
	{
		$this->author = $author;

		return $this;
	}

	/**
	 * Get author
	 *
	 * @return string 
	 */
	public function getAuthor()
	{
		return $this->author;
	}

	/**
	 * Set contain
	 *
	 * @param string $contain
	 * @return News
	 */
	public function setContain($contain)
	{
		$this->contain = $contain;

		return $this;
	}

	/**
	 * Get contain
	 *
	 * @return string 
	 */
	public function getContain()
	{
		return $this->contain;
	}

    /**
     * Set publication
     *
     * @param boolean $publication
     * @return News
     */
    public function setPublication($publication)
    {
        $this->publication = $publication;
    
        return $this;
    }

    /**
     * Get publication
     *
     * @return boolean 
     */
    public function getPublication()
    {
        return $this->publication;
    }
}