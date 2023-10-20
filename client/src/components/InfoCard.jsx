// Imports
import { Row, Col, Card } from "react-bootstrap"

const InfoCard = ({ headerTitle, contentTitle, children }) => {
    return <Row className="align-items-center justify-content-center my-5 mx-auto">
        <Col xs={12} md={10} lg={6}>
            <Card>
                <Card.Header as="h5" className="bg-primary text-white">
                    {headerTitle}
                </Card.Header>
                <Card.Body className="p-4">
                    <Card.Title className="mb-4 fw-bold fs-3">
                        {contentTitle}
                    </Card.Title>
                    {children}
                </Card.Body>
            </Card>
        </Col>
    </Row>
}

export default InfoCard