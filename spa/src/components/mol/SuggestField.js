import React, {createRef, Fragment} from 'react'

import {
  SuggestControl,
  Image,
  MenuControl,
  Pos,
  Box,
  Card,
  styled,
  Spacer,
  InputField,
  Input
} from '@qiwi/pijma-core'

import { Paragraph, Link, DropDown, MenuItem } from '@qiwi/pijma-desktop'
import {Icon, iconSearch} from './Icon'

const CardPos = Card.withComponent(Pos)
const CardItem = styled(Card)().withComponent(MenuItem)

const dropDownContainerRef = createRef()

export const SuggestField = ({
  equals = (a, b) => a === b,
  ...props
}) => (
  props.stub ? (
  <InputField
    stub
    input={false}
    active={false}
    title={props.title}
    help={props.help}
    hint={props.hint}
  />
  ) : (
  <SuggestControl
    value={props.value}
    suggest={props.suggest}
    items={props.items}
    empty={props.empty}
    equals={equals}
    onRequest={props.onRequest}
    onChange={props.onChange}
    onBlur={props.onBlur}
    onFocus={props.onFocus}
    onCancel={props.onCancel}
    children={(renderProps) => (
      <MenuControl
        count={renderProps.items.length}
        selected={renderProps.selected}
        onSelect={renderProps.onItemSelect}
        onKeyDown={renderProps.onItemKeyDown}
        children={(menuRenderProps) => (
          <Pos
            type="relative"
            ref={dropDownContainerRef}
            transition={`box-shadow ${renderProps.focused ? 300 : 200}ms cubic-bezier(0.4, 0.0, 0.2, 1)`}
          >
            <Box
              width={1}
              onMouseEnter={renderProps.onInputMouseEnter}
              onMouseLeave={renderProps.onInputMouseLeave}
            >
              <Input
                width={65}
                height={10}
                px={4} py={2}
                r="10px"
                b="none"
                bb={props.error ? '2px solid #d0021b' : 'none'}
                valueSize={4}
                valueWeight={300}
                valueColor={'#000'}
                placeholderSize={4}
                placeholderColor={'#666'}
                cursor={'text'}
                bg="#e6e6e6"
                transition={'all 100ms cubic-bezier(0.4, 0.0, 0.2, 1)'}

                value={props.suggest || ''}
                tabIndex={props.tabIndex}
                autoComplete={props.autoComplete}
                autoFocus={props.autoFocus}
                placeholder={props.placeholder}
                maxLength={props.maxLength}
                ref={renderProps.inputRef}
                error={String(!!props.error)}
                norb={String(props.items !== undefined && renderProps.focused && (props.items.length > 0 || props.empty !== undefined || props.loading))}
                hovered={String(renderProps.hovered)}
                focused={String(renderProps.focused)}
                onChange={renderProps.onRequest}
                onFocus={renderProps.onInputFocus}
                onBlur={renderProps.onInputBlur}
                onKeyDown={props.items !== undefined && renderProps.focused && (props.items.length > 0 || props.empty !== undefined) ? (
                  menuRenderProps.onKeyDown
                ) : (
                  renderProps.onItemKeyDown
                )}
              />
              <Pos
                type="absolute"
                cursor="pointer"
                right={4}
                top={2}
                onClick={renderProps.onSearchClick}
                children={<Icon icon={iconSearch}/>}
              />
            </Box>
            <DropDown
              target={renderProps.inputRef.current}
              container={dropDownContainerRef.current}
              minWidth={1}
              width={1}
              offset={3}
              show={props.items !== undefined && renderProps.focused && (props.items.length > 0 || props.empty !== undefined || props.loading === true)}
              rootClose={true}
              onHide={renderProps.onHide}
            >
              <CardPos
                ref={menuRenderProps.containerRef}
                maxHeight={98}
                bg="#fff"
                py={3}
                mx={-6}
                s="0 28px 52px 0 rgba(0, 0, 0, 0.16)"
                r={10}
                overflow="auto"
                onMouseDown={renderProps.onResultItemsMouseDown}
              >
                {props.loading ? (
                  Array(4).fill(1).map((_item, key) => (
                    <CardItem key={key} icon={true} stub text="stub" notes="stub"/>
                  ))
                ) : (
                  <Spacer size="s">
                    {menuRenderProps.items.length > 0 ? (
                      <Fragment>
                        {menuRenderProps.items.map((item, key) => (
                          <CardItem
                            key={key}
                            ref={item.ref}
                            onClick={item.onClick}
                            onMouseDown={item.onMouseDown}
                            onMouseEnter={item.onMouseEnter}
                            cursor="pointer"
                            text={renderProps.items[key].title}
                            notes={renderProps.items[key].description}
                            icon={renderProps.items[key].logo ? <Image width={6} height={6} src={renderProps.items[key].logo}/> : undefined}
                            hover={item.focused}
                            active={item.selected}
                            focus={item.selected}
                          />
                        ))}
                      </Fragment>
                    ) : null}
                    {props.empty && menuRenderProps.items.length === 0 ? (
                      <Box px={4}>
                        <Paragraph>
                          {props.empty.text}
                          {props.empty.link ? (
                            <Fragment>
                              {' '}
                              <Link
                                onClick={renderProps.onEmptyClick}
                                children={props.empty.link.text}
                              />
                            </Fragment>
                          ) : null}
                        </Paragraph>
                      </Box>
                    ) : null}
                  </Spacer>
                )}
              </CardPos>
            </DropDown>
          </Pos>
        )}
      />
    )}
    />
    )
    )

    SuggestField.defaultProps = {
      equals: (a, b) => a === b,
    }