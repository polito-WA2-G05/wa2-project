// Imports
import {useContext, useEffect, useState} from "react";

// Components
import {Loader} from '@components/layout'
import {ProductsTable} from '@components'

// Services
import api from "@services";

// Hooks
import {SessionContext} from "@contexts";
import {Col} from "react-bootstrap";

const PurchasedProducts = () => {
    const [purchases, setPurchases] = useState([]);
    const [loading, setLoading] = useState(true);

    const {onError} = useContext(SessionContext)

    useEffect(() => {
        api.product.getMyProducts()
            .then(purchases => setPurchases(purchases))
            .catch(onError)
            .finally(() => setLoading(false));
    }, []) // eslint-disable-line

    const purchasedProducts = purchases.flatMap((purchase) =>
        purchase.products.map((product) =>
            ({...product, purchasedAt: purchase.purchasedAt})
        )
    )

    if (!loading)
        return (
            <Col xs={12} lg={11} className='text-center'>
                {purchasedProducts.length === 0 ? <h4 className={"fw-bold"}>No purchased products have been found</h4> :
                    <>
                        <h1 className={"fw-bold my-5"}>My purchased products</h1>
                        <ProductsTable purchased products={purchasedProducts}/>
                    </>
                }
            </Col>
        )

    return <Loader/>
};

export default PurchasedProducts