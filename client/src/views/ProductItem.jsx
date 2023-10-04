// Imports
import {useEffect, useState} from 'react'
import {useLocation, useParams, Navigate} from "react-router-dom";
import {Spinner} from "react-bootstrap";
import {ProductInfo} from "../components"

// Services
import api from '../services/api'

// Hooks
import useNotification from "../hooks/useNotification";

const Products = () => {
    const {ean} = useParams()
    const location = useLocation()
    const {state} = location

    const [product, setProduct] = useState(null)
    const [loading, setLoading] = useState(true)

    const notify = useNotification()

    useEffect(() => {
        if (loading) {
            if (!state?.product) {
                api.getProductByEAN(ean)
                    .then((product) => setProduct(product))
                    .catch((err) => notify.error(err.detail))
                    .finally(() => setLoading(false))
            } else {
                setProduct(state.product)
                setLoading(false)
            }
        }
    }, [])

    if (!loading)
        return (
            <>
                {
                    product ? (
                        <>
                            <h2 className={"mb-4 fw-bold text-center"}>Product Information</h2>
                            <ProductInfo entity={product}/>
                        </>

                    ) : <Navigate to={"/"} replace/>
                }
            </>
        )

    return <div className="d-flex justify-content-center align-items-center w-100">
        <Spinner animation='border' size='xl' as='span' role='status' aria-hidden='true' className='me-2'/>
        <h2>Loading...</h2>
    </div>
}

export default Products