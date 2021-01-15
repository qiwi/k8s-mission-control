import React, {useEffect, useState} from "react"
import {Set} from "immutable"
import * as lodash from "lodash"

import {Block, Box, Flex, FlexItem} from "@qiwi/pijma-core"
import {Caption} from "@qiwi/pijma-desktop"

import {loadLog} from "../../../modules/containers/actions"
import {StatusIcon} from "../../mol/Icon"
import {LogPanel} from "./LogPanel"

const startLogCount = 300
const addLogCount = 30
const maxElementForStore = 5000

function hasActiveContainer(activeContainer) {
    return activeContainer !== undefined
}

function mergeLogs(existLogs, newLogs) {
    let result
    if (existLogs.length === 0) {
        return newLogs.reverse()
    }
    let existSet = new Set(existLogs.map(item => Date.parse(item.timestamp)))
    newLogs = newLogs.filter(item => !existSet.has(Date.parse(item.timestamp)))
    result = [...existLogs, ...newLogs.reverse()]
    if (result > maxElementForStore) {
        return lodash.take(result, maxElementForStore)
    }  else {
        return result
    }
}

const loadLogs = (cluster, ns, deployment, podName, container, logs) => {
    let lastDate, formattedDate, logCount
    if (logs.length === 0) {
        lastDate = new Date()
        lastDate.setDate(lastDate.getDate())
        formattedDate = lastDate.toISOString()
        logCount = startLogCount
    } else {
        lastDate = Date.parse(logs[logs.length-1].timestamp)
        formattedDate = new Date(lastDate).toISOString()
        logCount = addLogCount
    }
    return loadLog(cluster, ns, deployment, podName, container, logCount, formattedDate, true)
      .then(data => mergeLogs(logs, data.logList))
}

const LogsStatusHeader = ({count}) => <Flex
  align="center"
  wrap="wrap"
  mb={2} mt={2}
>
    <FlexItem mr={2} pb={1} style={{flex: "3%"}}>
        <StatusIcon status={{ value: 'OK' }} />
    </FlexItem>

    <FlexItem style={{flex: "15%", textAlign: "left"}}>
        <Caption color="default">Logs</Caption>
    </FlexItem>

    <FlexItem justify="right" style={{flex: "70%", textAlign: "end"}}>
        <Caption color="default">count: {count}</Caption>
    </FlexItem>
</Flex>

export const LogPanelWrapper = ({cluster, ns, deployment, podName, container}) => {

    const [logs, setLogs] = useState([])

    useEffect(() => {
        setLogs([])
        if (hasActiveContainer(container)) {
            loadLogs(cluster, ns, deployment, podName, container, [])
              .then(logs => setLogs(logs))
        }
    }, [cluster, ns, deployment, podName, container, setLogs])

    const fetchNewLogs = () => {
        loadLogs(cluster, ns, deployment, podName, container, logs)
          .then(logs => setLogs(logs))
    }

    return <FlexItem height="100%" ml="20px" width="calc(75% + -5px)">
            <Box mb="16px">
                <LogsStatusHeader count={logs.length} />
            </Box>

            <Block py={2} height="calc(100% + -50px)">
                <LogPanel items={logs} loader={fetchNewLogs} isContainerSelected={hasActiveContainer(container)}/>
            </Block>
        </FlexItem>
}