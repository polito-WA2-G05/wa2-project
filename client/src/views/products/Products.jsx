// Imports
import { useState, useEffect } from 'react'
import { Spinner } from 'react-bootstrap';

// Components
import { ProductsTable } from '@components'

// Services
import api from '@services'

// Hooks
import { useNotification } from '@hooks';

const Products = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const notify = useNotification()

    useEffect(() => {
        api.product.getAllProducts()
            .then((items) => {
                setProducts(items)
            })
            .catch((err) => {
                if (err.status === 404) {
                    setProducts([])
                } else { notify.error(err.detail ?? err) }
            })
            .finally(() => setLoading(false));
    }, [])


    if (!loading)
        return (
            <div className='text-center'>
                {products.length === 0 ? <h3 className={"fw-bold fs-2 mb-4"}>No products found</h3> :
                    <>
                        <h3 className={"fw-bold mb-4 fs-2 text-center"}>All products</h3>
                        <ProductsTable products={products} />
                    </>
                }
            </div>
        )

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2' />
        <h2>Loading...</h2>
    </div>
}

export default Products