import React from 'react'

import {Link} from '@qiwi/pijma-desktop'

import {Card, CardContent, CardHeader} from '../../mol/Card'

import {MessagesTable} from '../../org/Messages'

export const MessagesCard = ({data: {allMessages, collapsedMessages}}) => {
  const [showCollapsed, setShowCollapsed] = React.useState(false)

  const collapsedCount = allMessages.length - collapsedMessages.length
  const hasCollapsed = Boolean(collapsedCount)

  const showShowMessagesAction = hasCollapsed && !showCollapsed
  const showCollapseMessagesAction = hasCollapsed && showCollapsed

  const messages = showCollapsed ? allMessages : collapsedMessages

  return <Card>
    <CardHeader text="Messages">
      { showShowMessagesAction && <Link size="s" onClick={() => setShowCollapsed(true)}>Show hidden {collapsedCount} messages</Link> }
      { showCollapseMessagesAction && <Link size="s" onClick={() => setShowCollapsed(false)}>Collapse {collapsedCount} messages</Link> }
    </CardHeader>
    <CardContent expand render={() => <MessagesTable messages={messages} showResourceColumn={true} />} />
  </Card>
}