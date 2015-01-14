<?php

namespace Epi\BoutiqueBundle\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Distribution
 *
 * @ORM\Table()
 * @ORM\Entity
 */
class Distribution
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
     * @ORM\Column(name="date", type="string", length=255)
     */
    private $date;

     /**
     * @var string
     *
     * @ORM\Column(name="horaires", type="string", length=255)
     */
    private $horaires;


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
     * @param string $date
     * @return Distribution
     */
    public function setDate($date)
    {
        $this->date = $date;
    
        return $this;
    }
     /**
     * Get date
     *
     * @return string 
     */
    public function getDate()
    {
        return $this->date;
    }

    /**
     *
     * @return string 
     */
    public function getHoraires()
    {
        return $this->horaires;
    }

    /**
     *
     * @return Distribution
     */
    public function setHoraires($horaires)
    {
        $this->horaires = $horaires;
    
        return $this;
    }

    public function __toString(){
        return "le ".$this->date.", ".$this->horaires;
    }

   


}
