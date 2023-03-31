// Imports
import {useState, useEffect} from 'react'

// Components
import {ProductTable} from '../components/index'
import {Spinner} from 'react-bootstrap';

// Services
import api from '../services/api'

const Products = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        api.getAllProducts()
            .then((items) => {
                setProducts(items)
            })
            .catch((err) => {
                err.status === 404 && setProducts([]);
                // err.status === 500 && setError({ show: true, ...err })
            })
            .finally(() => setLoading(false));
    }, [])


    if (!loading)
        return (
            <>
                <h3 className={"fw-bold fs-2 mb-4"}>All products</h3>
                <ProductTable products={products}/>
            </>
        )

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2'/>
        <h2>Loading...</h2>
    </div>
}

export default Products