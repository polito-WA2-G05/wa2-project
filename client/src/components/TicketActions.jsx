// Imports
import { useState } from "react";
import { Button, Modal, Form } from "react-bootstrap";
import { ResolveModal, PriorityModal, SurveyModal } from "@components"
import {useNavigate} from 'react-router-dom'

// Hooks
import { useSessionStorage, useNotification } from "@hooks";

// Services
import api from "@services";

// Styles
import { ImBubble } from "react-icons/im"
import { HiArchiveBoxXMark, HiUserPlus } from "react-icons/hi2"
import { BsFillTrashFill, BsSend, BsArrowCounterclockwise, BsFillSignStopFill, BsFillPatchCheckFill } from "react-icons/bs"

// Utils
import { TicketStatus, Role } from "@utils";

const TicketActions = ({ ticket, handleUpdate }) => {
    const { session } = useSessionStorage();
    const navigate = useNavigate()
    const notify = useNotification();
    const authority = session.details.authorities[0];

    const [showResolveModal, setShowResolveModal] = useState(false);
    const [showSurveyModal, setShowSurveyModal] = useState(false);
    const [showPriorityModal, setShowPriorityModal] = useState(false);

    const handleTicketAction = (action, ...args) => {
        const actionMap = {
            cancel: api.ticket.cancelTicket,
            close: api.ticket.closeTicket,
            reopen: api.ticket.reopenTicket,
            inProgress: api.ticket.inProgressTicket,
            stop: api.ticket.stopTicket,
            resolve: api.ticket.resolveTicket,
        };
        const actionFunction = actionMap[action];

        if (actionFunction) {
            actionFunction(...args)
                .then((ticket) => {
                    console.log(ticket)
                    handleUpdate(ticket);
                    notify.success("Ticket has been successfully updated");
                })
                .catch((err) => notify.error(err.detail ?? err));
        }
    };

    const handleConfirmResolveModal = (description) => {
        handleTicketAction("resolve", authority, ticket.id, description);
    }

    const handleConfirmPriorityModal = (priorityLevel) => {
        handleTicketAction("inProgress", priorityLevel, ticket.id)
    }

    const handleConfirmSurveyModal = (ticket) => {
        handleUpdate(ticket)
    }

    return (
        <div>
            {authority === Role.MANAGER && (
                <>
                    {ticket.status === TicketStatus.OPEN || ticket.status === TicketStatus.REOPENED ? (
                        <>
                            <Button variant="success" onClick={() => setShowResolveModal(true)}>
                                <BsFillPatchCheckFill size={15} className="me-2" />
                                Resolve Ticket
                            </Button>
                            <ResolveModal
                                show={showResolveModal}
                                ticketId={ticket.id}
                                onHide={() => setShowResolveModal(false)}
                                onConfirm={handleConfirmResolveModal}
                            />
                            <Button className="mx-3" onClick={() => handleTicketAction("close", authority, ticket.id)} variant="danger">
                                <HiArchiveBoxXMark size={15} className="me-2" />
                                Close Ticket
                            </Button>
                            <Button onClick={() => setShowPriorityModal(true)}>
                                <HiUserPlus size={15} className="me-2" />
                                Assign Expert
                            </Button>
                            <PriorityModal
                                show={showPriorityModal}
                                onHide={() => setShowPriorityModal(false)}
                                onConfirm={handleConfirmPriorityModal} />
                        </>
                    ) : (
                        ticket.status === TicketStatus.RESOLVED && ticket.survey && (
                            <Button className="mx-3" onClick={() => handleTicketAction("close", authority, ticket.id)} variant="danger">
                                <HiArchiveBoxXMark size={15} className="me-2" />
                                Close Ticket
                            </Button>
                        )
                    )}
                </>
            )}

            {authority === Role.CUSTOMER && (
                <>
                    {ticket.status === TicketStatus.IN_PROGRESS && (
                        <Button className="me-3" onClick={() => navigate(`/tickets/${ticket.id}/chat`)} style={{ backgroundColor: '#32CD32', color: '#FFFFFF' }}>
                            <ImBubble size={15} className="me-2" />
                            Chat
                        </Button>
                    )}
                    {(ticket.status === TicketStatus.RESOLVED && !ticket.survey) && (
                        <>
                            <Button className="me-3" onClick={() => setShowSurveyModal(true)} variant="success">
                                <BsSend size={15} className="me-2" />
                                Send Survey
                            </Button>
                            <SurveyModal
                                show={showSurveyModal}
                                onHide={() => setShowSurveyModal(false)}
                                onConfirm={handleConfirmSurveyModal}
                                ticketId = {ticket.id} />
                        </>
                    )}
                    {(ticket.status === TicketStatus.CLOSED || ticket.status === TicketStatus.RESOLVED) && (
                        <Button className="me-3" onClick={() => handleTicketAction("reopen", ticket.id)} variant="warning">
                            <BsArrowCounterclockwise size={15} className="me-2" />
                            Reopen Ticket
                        </Button>
                    )}
                    {[TicketStatus.OPEN, TicketStatus.REOPENED, TicketStatus.IN_PROGRESS].includes(ticket.status) &&
                        <Button onClick={() => handleTicketAction("cancel", ticket.id)} variant="danger">
                            <BsFillTrashFill size={15} className="me-2" />
                            Cancel Ticket
                        </Button>
                    }
                </>
            )}
            {ticket.status === TicketStatus.IN_PROGRESS && authority === Role.EXPERT && (
                <>
                    <Button onClick={() => handleTicketAction("stop", ticket.id)}>
                        <BsFillSignStopFill size={15} className="me-2" />
                        Stop Ticket
                    </Button>
                    <Button className="mx-3" onClick={() => handleTicketAction("close", authority, ticket.id)} variant="danger">
                        <HiArchiveBoxXMark size={15} className="me-2" />
                        Close Ticket
                    </Button>
                    <Button variant="success" onClick={() => handleTicketAction("resolve", authority, ticket.id, null)}>
                        <BsFillPatchCheckFill size={15} className="me-2" />
                        Resolve Ticket
                    </Button>
                    <Button className="me-3" onClick={() => { }} style={{ backgroundColor: '#32CD32', color: '#FFFFFF' }}>
                        <ImBubble size={15} className="me-2" />
                        Chat
                    </Button>
                </>
            )}
        </div>
    );
};

export default TicketActions;
