<?php

namespace Epi\BoutiqueBundle\Form;

use Symfony\Component\Form\AbstractType;
use Symfony\Component\Form\FormBuilderInterface;
use Symfony\Component\OptionsResolver\OptionsResolverInterface;

class ProduitType extends AbstractType
{
    public function buildForm(FormBuilderInterface $builder, array $options)
    {
        $builder
            ->add('nom')
            ->add('description')
            ->add('quantite')
            ->add('prix')
            ->add('quantiteCommandee')
            ->add('image', 'file')
            ->add('categorie')
        ;
    }

    public function setDefaultOptions(OptionsResolverInterface $resolver)
    {
        $resolver->setDefaults(array(
            'data_class' => 'Epi\BoutiqueBundle\Entity\Produit'
        ));
    }

    public function getName()
    {
        return 'epi_boutiquebundle_produittype';
    }
}
