import React, {useState}  from 'react'

import {Box, Spacer} from '@qiwi/pijma-core'
import {SimpleModal, Heading, Text} from '@qiwi/pijma-desktop'

import {MonospacedText, DecoratedText} from './Text'

export const Annotation = ({ name, value, hideValue }) => {
  const [modal, setModal] = useState(false)

  if (!hideValue) {
    return <Text>
      {name}: {value}
    </Text>
  }

  return <>
    {modal && <SimpleModal show={true} size="m" closable backdropClose
                           onHide={() => setModal(false)}>
      <Spacer size="m">
        <Heading size="5">{name}</Heading>
        <MonospacedText>{value}</MonospacedText>
      </Spacer>
    </SimpleModal>}
    <DecoratedText onClick={() => setModal(true)}>
      {name}
      {!hideValue && `: ${value}`}
    </DecoratedText>
  </>
}

export const AnnotationsList = ({ items, hideValues }) => <>
  {items.map(({ name, key, value }, i) => <Box key={i} mr={2} display="inline-block">
    <Annotation name={key || name} value={value} hideValue={hideValues} />
  </Box>)}
</>