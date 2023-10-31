// Imports
import {useEffect, useState} from 'react'
import {Col} from "react-bootstrap";

// Components
import {Loader} from '@components/layout'
import {ProductsTable} from '@components'

// Services
import api from '@services'

// Hooks
import {useNotification} from '@hooks';

const Products = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    const notify = useNotification()

    useEffect(() => {
        api.product.getAllProducts()
            .then((items) => setProducts(items))
            .catch((err) => notify.error(err.detail ?? err))
            .finally(() => setLoading(false));
    }, []) // eslint-disable-line

    if (!loading)
        return (
            <>
                {products.length === 0 ? <h4 className={"text-center fw-bold"}>No products have been found</h4> :
                    <Col xs={12} lg={11} className='text-center align-self-start'>
                        <h1 className={"fw-bold my-5"}>All products</h1>
                        <ProductsTable products={products}/>
                    </Col>
                }
            </>
        )

    return <Loader/>
}

export default Products