// Imports
import {Card} from "react-bootstrap"

const InfoCard = ({headerTitle, contentTitle, children}) => {
    return <Card>
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

}

export default InfoCard