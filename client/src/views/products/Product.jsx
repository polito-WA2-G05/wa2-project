// Imports
import {useEffect, useState} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {Button, Card, Col, Row} from "react-bootstrap";

// Components
import {Loader} from "@components/layout";
import {InfoCard} from "@components";

// Services
import api from "@services";

// Hooks
import {useNotification} from "@hooks";

const Product = () => {
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);

    const {ean} = useParams();
    const navigate = useNavigate();

    const notify = useNotification();

    useEffect(() => {
        if (loading) {
            api.product.getProductByEAN(ean)
                .then(product => setProduct(product))
                .catch((err) => err?.status !== 404 && notify.error(err.detail ?? err))
                .finally(() => setLoading(false));
        }
    }, []); // eslint-disable-line

    const onGoBack = () => navigate(-1, {replace: true})

    if (!loading)
        return (
            <>
                {product ? (
                    <Row className="my-5 align-self-start">
                        <Col xs={2} className={"d-flex justify-content-center align-items-start"}>
                            <Button variant={"outline-primary"} onClick={onGoBack}
                                    className="py-2 px-5 rounded-3 fw-semibold mx-auto">
                                Go Back
                            </Button>
                        </Col>
                        <Col xs={12} lg={{span: 8}}>
                            <InfoCard headerTitle={`Product Info`} contentTitle={`Product EAN #${product.ean}`}>
                                {[
                                    {label: "Name", value: product.name},
                                    {label: "Brand", value: product.brand}
                                ].map((info) => (
                                    <Card.Text key={`product-${ean}-${info.label}`}>
                                        <strong>{info.label}</strong>
                                        <p>{info.value}</p>
                                    </Card.Text>
                                ))}
                            </InfoCard>
                        </Col>
                    </Row>
                ) : (
                    <div className="d-flex flex-column align-items-center">
                        <h4 className={"fw-bold"}>Product #{ean} not found</h4>
                        <Button onClick={onGoBack} className="py-2 px-5 rounded-3 my-5 fw-semibold">
                            Go Back
                        </Button>
                    </div>
                )}
            </>
        );

    return <Loader/>
};

export default Product;